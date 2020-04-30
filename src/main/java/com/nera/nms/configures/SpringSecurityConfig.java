/**
 * 
 */
package com.nera.nms.configures;



import com.nera.nms.constants.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.nera.nms.dto.ActiveUserStore;
import com.nera.nms.repositories.ISystemAuditLogRepository;
import com.nera.nms.repositories.IUserRepository;
import com.nera.nms.services.UserService;

/**
 * @author Martin Do
 *
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String PATH_NERA = "nera/**";

    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
    }

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    ISystemAuditLogRepository iSystemAuditLogRepository;

    @Autowired
    ActiveUserStore activeUserStore;
    /**
     * custom 403 access denied handler
     */
    @Autowired
    @Qualifier("persistentTokenRepository")
    private PersistentTokenRepository persistentTokenRepository;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.headers().frameOptions().sameOrigin();
        http.csrf().disable()
                .authorizeRequests()
                    .antMatchers(
                            "/js/**",
                            "/css/**",
                            "/images/**").permitAll()
                    .antMatchers("/home", "/about").permitAll()
                    .antMatchers(HttpMethod.GET, CommonConstants.PATH_DELIMITER +PATH_NERA).permitAll()
					.antMatchers(HttpMethod.POST, CommonConstants.PATH_DELIMITER +PATH_NERA).permitAll()
					.antMatchers(HttpMethod.PUT, CommonConstants.PATH_DELIMITER +PATH_NERA).permitAll()
					.antMatchers(HttpMethod.PATCH, CommonConstants.PATH_DELIMITER +PATH_NERA).permitAll()
                    .antMatchers(HttpMethod.DELETE, CommonConstants.PATH_DELIMITER +PATH_NERA).permitAll()
                    .antMatchers("/actuator/**").hasAuthority("ACTUATOR")
                    .anyRequest()
                    .authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .successHandler(savedRequestAwareAuthenticationSuccessHandler())
                    .successHandler(new LoginStatusHandler(iUserRepository, iSystemAuditLogRepository, activeUserStore))
                    .permitAll()                    
                    .and()
                .sessionManagement()
                    .maximumSessions(1).sessionRegistry(sessionRegistry()).and()
                    .sessionFixation().none()
                    .and()
                .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .addLogoutHandler(new LogoutStatusHandler(iUserRepository, iSystemAuditLogRepository, activeUserStore))
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
                    .and()
                    .rememberMe().rememberMeParameter("remember-me")
                    .tokenRepository(persistentTokenRepository).userDetailsService(userService)
                    .and()
                .exceptionHandling().accessDeniedPage("/403");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
    
    @Bean
	public SavedRequestAwareAuthenticationSuccessHandler 
                savedRequestAwareAuthenticationSuccessHandler() {
               SavedRequestAwareAuthenticationSuccessHandler auth
                    = new SavedRequestAwareAuthenticationSuccessHandler();
		auth.setTargetUrlParameter("targetUrl");
		return auth;
	}
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}