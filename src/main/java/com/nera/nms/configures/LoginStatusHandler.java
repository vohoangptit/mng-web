package com.nera.nms.configures;

import com.nera.nms.components.CommonMethodComponent;
import com.nera.nms.components.LoggedUserComponent;
import com.nera.nms.constants.CommonConstants;
import com.nera.nms.constants.UserConstants;
import com.nera.nms.dto.ActiveUserStore;
import com.nera.nms.dto.UserProfileDto;
import com.nera.nms.models.Group;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.models.User;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.repositories.IUserRepository;
import com.nera.nms.utils.UserConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("LoginStatusHandler")
@Slf4j
public class LoginStatusHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler, ApplicationListener<AbstractAuthenticationEvent> {

    private IUserRepository iUserRepository;
    private ISystemAuditLogRepository iSystemAuditLogRepository;
    private ActiveUserStore activeUserStore;
    private boolean userGroupIsActive = false;

    private static final String LOGIN_COUNT = "login_count";
    private static final String LAST_LOGIN = "last_login";

    private SimpleDateFormat frm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public LoginStatusHandler(IUserRepository iUserRepository, ISystemAuditLogRepository iSystemAuditLogRepository, ActiveUserStore activeUserStore) {
        this.iUserRepository = iUserRepository;
        this.iSystemAuditLogRepository = iSystemAuditLogRepository;
        this.activeUserStore = activeUserStore;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException {

        request.setAttribute("user", auth.getPrincipal());

        //log user
        LoggedUserComponent user = new LoggedUserComponent(auth.getName(), activeUserStore);
        log.info(StringUtils.join("user login: ",user.getUsername()));

        //user add list
        Object optionUser = iUserRepository.findUserDetail(user.getUsername());
        List<String> listPermission = new ArrayList<>(iUserRepository.selectPermissionByEmail(user.getUsername()));
        String permission = listPermission.stream().collect(Collectors.joining(CommonConstants.COMMA));

        User userObj = iUserRepository.selectUserByEmail(user.getUsername());
        List <Group> groupList = userObj.getGroups();
        groupList.forEach(group->{
            if (group.isActive()) {
                userGroupIsActive = true;
            }
        });
        if (!userGroupIsActive) {
            response.sendRedirect("/login?error");
            return;
        }

        UserProfileDto userProfileDto = UserConvertUtil.convertUserOptionToDto(optionUser);
        if(null == permission){
            permission = "";
        }
        userProfileDto.setPermission(permission);
        userProfileDto.addToSession(request.getSession(true));

        // set menu playbook approved to session
        int countPlaybookApproved;
        countPlaybookApproved = iUserRepository.countPlaybookByStatusNew();
        request.getSession(true).setAttribute("playbookApproveMenu", "Playbook Approval ("+ countPlaybookApproved +")");
        //check session null
        if(request.getSession().getAttribute(CommonConstants.USER_PROFILE)==null){
            response.sendRedirect("/login");
        }
        if(userObj.getFirstLogin()==true){
            response.sendRedirect("/password/change");
        } else{
            response.sendRedirect("/menu/dashboard");
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException auth) throws IOException {
        request.getSession(true).setAttribute("error", auth.getMessage());
        request.setAttribute("error", auth.getMessage());
        response.sendRedirect("login.html?error=true");

    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent abstractAuthenticationEvent) {
        if (abstractAuthenticationEvent instanceof AbstractAuthenticationFailureEvent) {
            String email = abstractAuthenticationEvent.getAuthentication().getName();
            User userEntity = iUserRepository.selectUserByEmail(email);
            if(userEntity == null) return;
            long diffLoginTime = 0;
            if (countUserLogin(email, userEntity, diffLoginTime)){
                return;
            }
            //save log to DB
            CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
            StringBuilder userGroup = new StringBuilder();
            commonMethodComponent.auditGroupNameFormat(userGroup, userEntity);

            SystemAuditLog systemAuditLog = new SystemAuditLog();
            commonMethodComponent.initSystemAuditLogData(systemAuditLog
                    , userEntity
                    , userGroup.toString()
                    , UserConstants.IS_LOGGIN
                    , !UserConstants.IS_AUTHENTICAION);
            iSystemAuditLogRepository.save(systemAuditLog);
            log.warn("Detected an invalid login");

        } else if (abstractAuthenticationEvent instanceof AuthenticationSuccessEvent) {
            String email = abstractAuthenticationEvent.getAuthentication().getName();
            User userEntity = iUserRepository.selectUserByEmail(email);
            if(userEntity == null) return;
            long loginTime = userEntity.getLoginCount();
            if(loginTime < 5) {
                userEntity.setLoginCount(0);
                userEntity.setLastLoginDateTime(new Timestamp(System.currentTimeMillis()));
                iUserRepository.save(userEntity);
            }

            //save log to DB
            CommonMethodComponent commonMethodComponent = new CommonMethodComponent();
            StringBuilder userGroup = new StringBuilder();
            commonMethodComponent.auditGroupNameFormat(userGroup, userEntity);

            SystemAuditLog systemAuditLog = new SystemAuditLog();
            commonMethodComponent.initSystemAuditLogData(systemAuditLog
                    , userEntity
                    , userGroup.toString()
                    , UserConstants.IS_LOGGIN
                    , UserConstants.IS_AUTHENTICAION);
            iSystemAuditLogRepository.save(systemAuditLog);
            log.info("A user logged in successfully");
        }
    }

    private boolean countUserLogin(String email, User userEntity, long diffLoginTime) {
        long countLogin;
        String lastLogin;
        Jedis jedis = new Jedis();
        try {
            if (jedis.hget(email, LOGIN_COUNT) != null) {
                countLogin = Long.parseLong(jedis.hget(email, LOGIN_COUNT));
            } else {
                countLogin = 0L;
            }
            if (jedis.hget(email, LAST_LOGIN) != null) {
                lastLogin = jedis.hget(email, LAST_LOGIN);
                if(lastLogin == null){
                    return true;
                }
                diffLoginTime = TimeUnit.MILLISECONDS.toMinutes(new Date().getTime() - frm.parse(lastLogin).getTime());
            }
            if (diffLoginTime <= 60) {
                countLogin = countLogin + 1L;
            } else {
                countLogin = 1L;
            }
            if((int) countLogin >= 5) {
                // lock user
                userEntity.setActive(false);
                userEntity.setLoginCount(countLogin);
                String searchString = userEntity.getSearchString();
                searchString = searchString.replace(UserConstants.ACTIVE, UserConstants.BLOCKED);
                iUserRepository.saveStatus(searchString, userEntity.isActive(), userEntity.getId());

                //reset count
                jedis.hset(email, LOGIN_COUNT, Long.toString(0));
                jedis.hset(email, LAST_LOGIN, frm.format(new Date()));
            } else{
                //save login_count to redis
                jedis.hset(email, LOGIN_COUNT, Long.toString(countLogin));
                jedis.hset(email, LAST_LOGIN, frm.format(new Date()));
            }
        } catch (Exception e) {
            log.error("Exception LoginStatusHandler.countUserLogin : ", e);
        } finally {
            jedis.close();
        }
        return false;
    }
}
