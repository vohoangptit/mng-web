package com.nera.nms.repositories.impl;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.models.User;
import com.nera.nms.models.UserDepartment;
import com.nera.nms.models.UserJobTitle;
import com.nera.nms.repositories.IUserDepartmentRepository;
import com.nera.nms.repositories.IUserJobTitleRepository;
import com.nera.nms.utils.BEDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type User dao.
 */
@Slf4j
@Repository
public class UserDaoImpl {
	/**
	 * The Em.
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * The constant logger.
	 */
	public static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

	/**
	 * The User department repository.
	 */
	@Autowired
	private IUserDepartmentRepository iUserDepartmentRepository;

	/**
	 * The User job title repository.
	 */
	@Autowired
	private IUserJobTitleRepository iUserJobTitleRepository;

	/**
	 * The Active.
	 */
	private static final String ACTIVE = "active";

	/**
	 * The Filtered from last login.
	 */
	private static final String FILTERED_FROM_LAST_LOGIN  = "filteredFromLastLogin";

	/**
	 * The Filtered to last login.
	 */
	private static final String FILTERED_TO_LAST_LOGIN = "filteredToLastLogin";

	/**
	 * The Filtered search string.
	 */
	private static final String FILTERED_SEARCH_STRING = "searchText";

	/**
	 * The Filtered job title.
	 */
	private static final String FILTERED_JOB_TITLE = "filteredJobTitleIds";

	/**
	 * The Filtered department.
	 */
	private static final String FILTERED_DEPARTMENT = "filteredDepartmentIds";

	/**
	 * The Filtered user group.
	 */
	private static final String FILTERED_USER_GROUP = "filteredUserGroupIds";

	public long findUserWithQuery(String page, String pageSize, String sortBy, String orderBy, final List <User> userList, Map<String, String> mapCondition) {

    	int pageSizeInt = Integer.parseInt(pageSize);
		int pageInt = (Integer.parseInt(page) - 1) * pageSizeInt;

    	List<UserDepartment> departmentList = iUserDepartmentRepository.findAllByOrderByDepartmentName();
    	Map<Long, String> departmentMap = departmentList.stream().collect(Collectors.toMap(UserDepartment::getId, UserDepartment::getDepartmentName));

    	List<UserJobTitle> jobTitleList = iUserJobTitleRepository.findAllByOrderByJobTitleName();
    	Map<Long, String> jobTitleMap = jobTitleList.stream().collect(Collectors.toMap(UserJobTitle::getId, UserJobTitle::getJobTitleName));


    	em.getEntityManagerFactory();

		StringBuilder querySQLStr = getQueryString(sortBy, mapCondition);

		StringBuilder countQueryStr = new StringBuilder("SELECT COUNT(u.id) FROM user_user u ").append(querySQLStr);

    	StringBuilder queryStr = new StringBuilder("SELECT * FROM user_user u").append(querySQLStr);

    	String sortByColName = "";
    	switch(sortBy) {
			case "email":
				sortByColName = "email";
				break;
			case "mobileNumber":
				sortByColName = "mobile_number";
				break;
			case "groupNames":
				sortByColName = "group_name";
				break;
			case "departmentName":
				sortByColName = "department_name";
				break;
			case "jobTitleName":
				sortByColName = "job_title_name";
				break;
			case CommonConstants.STATUS:
				sortByColName = "u.is_active";
				break;
			case "formattedLastLogin":
				sortByColName = "last_login_date_time";
				break;
			default:
				sortByColName = "fullName";
				break;
    	}

    	queryStr.append(" AND u.id <> " + CommonConstants.SUPER_ADMIN_ID);
    	countQueryStr.append(" AND u.id <> " + CommonConstants.SUPER_ADMIN_ID);

    	queryStr.append(" ORDER BY " + sortByColName + " ");
		queryStr.append(orderBy);
    	Query querySQL = em.createNativeQuery(queryStr.toString(), User.class);
    	Query countQuerySQL = em.createNativeQuery(countQueryStr.toString());

		setQuery(querySQL, mapCondition);
		setQuery(countQuerySQL, mapCondition);

    	querySQL.setFirstResult(pageInt);
    	querySQL.setMaxResults(pageSizeInt);

    	userList.addAll(querySQL.getResultList());
    	long countResults = Long.parseLong(countQuerySQL.getSingleResult().toString());


    	userList.forEach(user -> {
    		StringBuilder groupNames = new StringBuilder();
    		user.getGroups().forEach(groupName -> {

    			if(!groupName.isDeleted()) {
    				if (groupNames.length() > 0) {
        				groupNames.append(", ");
        			}
        			groupNames.append(groupName.getGroupName());
    			}
    		});
    		user.setGroupNames(groupNames.toString());
    		if (user.getDepartmentId() != null) {
    			user.setDepartmentName(departmentMap.get(user.getDepartmentId()));
    		}

    		if (user.getJobTitleId() != null) {
    			user.setJobTitleName(jobTitleMap.get(user.getJobTitleId()));
    		}
    	});

    	return countResults;
	}

	private void setQuery(Query querySQL, Map<String, String> mapCondition) {
		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_SEARCH_STRING))) {
			if (mapCondition.get(FILTERED_SEARCH_STRING).toLowerCase().contains(ACTIVE)) {
				querySQL.setParameter("text", "% " + mapCondition.get(FILTERED_SEARCH_STRING) + " %"); //note: need to have space after and before %
			} else {
				querySQL.setParameter("text", mapCondition.get(FILTERED_SEARCH_STRING));
			}
		}
		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_USER_GROUP))) {
			List <String> groupIds = Arrays.asList(mapCondition.get(FILTERED_USER_GROUP).split(CommonConstants.COMMA));
			List <Long> groupIdsList = new ArrayList <>();
			groupIds.forEach(groupId -> groupIdsList.add(Long.parseLong(groupId)));
			querySQL.setParameter(FILTERED_USER_GROUP, groupIdsList);
		}
		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_DEPARTMENT))) {
			List <String> departmentIds = Arrays.asList(mapCondition.get(FILTERED_DEPARTMENT).split(CommonConstants.COMMA));
			List <Long> departmentIdsList = new ArrayList <>();
			departmentIds.forEach(departmentId -> departmentIdsList.add(Long.parseLong(departmentId)));
			querySQL.setParameter(FILTERED_DEPARTMENT, departmentIdsList);
		}
		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_JOB_TITLE))) {
			List <String> jobTitleIds = Arrays.asList(mapCondition.get(FILTERED_JOB_TITLE).split(CommonConstants.COMMA));
			List <Long> jobTitleIdsList = new ArrayList <>();
			jobTitleIds.forEach(jobTitleId -> jobTitleIdsList.add(Long.parseLong(jobTitleId)));
			querySQL.setParameter(FILTERED_JOB_TITLE, jobTitleIdsList);
		}
		try {
			if (StringUtils.isNotBlank(mapCondition.get(FILTERED_FROM_LAST_LOGIN)) && StringUtils.isNotBlank(mapCondition.get(FILTERED_TO_LAST_LOGIN))) {
				String newFromLastLogin = mapCondition.get(FILTERED_FROM_LAST_LOGIN) + " 00:00:00";
				String newToLastLogin = mapCondition.get(FILTERED_TO_LAST_LOGIN) + " 23:59:59";
				querySQL.setParameter(FILTERED_FROM_LAST_LOGIN, BEDateUtils.formatDate(newFromLastLogin, CommonConstants.DATE_TIME_SEC_FORMAT));
				querySQL.setParameter(FILTERED_TO_LAST_LOGIN, BEDateUtils.formatDate(newToLastLogin, CommonConstants.DATE_TIME_SEC_FORMAT));
			} else if (StringUtils.isNotBlank(mapCondition.get(FILTERED_FROM_LAST_LOGIN))) {
				String newFromLastLogin = mapCondition.get(FILTERED_FROM_LAST_LOGIN) + " 00:00:00";
				querySQL.setParameter(FILTERED_FROM_LAST_LOGIN, BEDateUtils.formatDate(newFromLastLogin, CommonConstants.DATE_TIME_SEC_FORMAT));
			} else if (StringUtils.isNotBlank(mapCondition.get(FILTERED_TO_LAST_LOGIN))) {
				String newToLastLogin = mapCondition.get(FILTERED_TO_LAST_LOGIN) + " 23:59:59";
				querySQL.setParameter(FILTERED_TO_LAST_LOGIN, BEDateUtils.formatDate(newToLastLogin, CommonConstants.DATE_TIME_SEC_FORMAT));
			}
		} catch (ParseException e) {
			logger.error("AuditLogDaoImpl.setQuery : ", e);
		}
	}

	private StringBuilder getQueryString(String sortBy, Map<String, String> mapCondition) {
		StringBuilder querySQLStr = new StringBuilder(StringUtils.EMPTY);

		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_USER_GROUP))) {
			querySQLStr.append(" LEFT JOIN ( SELECT * FROM user_group_user WHERE user_group_id In :filteredUserGroupIds GROUP BY user_id ) ugu");
		} else {
			querySQLStr.append(" LEFT JOIN ( SELECT * FROM user_group_user GROUP BY user_id ) ugu");
		}

		querySQLStr.append(" ON u.id = ugu.user_id");

		querySQLStr.append(" LEFT JOIN user_group ug ON ug.id = ugu.user_group_id");

		if (sortBy.equalsIgnoreCase("departmentName")) {
			querySQLStr.append(" LEFT JOIN user_department ud ON ud.id = u.department_id");
		}

		if (sortBy.equalsIgnoreCase("jobTitleName")) {
			querySQLStr.append(" LEFT JOIN user_job_title ujt ON ujt.id = u.job_title_id");
		}

		querySQLStr.append(" WHERE u.is_delete = 'N'");

		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_SEARCH_STRING))) {
			querySQLStr.append(" AND u.search_string LIKE '%' :text '%'");
		}

		if (mapCondition.get(FILTERED_SEARCH_STRING).toLowerCase().contains(ACTIVE)) {
			querySQLStr.append("AND u.search_string LIKE :text ");
		}

		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_USER_GROUP))) {
			  querySQLStr.append(" AND ugu.user_group_id IN :filteredUserGroupIds");
		}

		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_DEPARTMENT))) {
			querySQLStr.append(" AND u.department_id IN :filteredDepartmentIds");
		}

		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_JOB_TITLE))) {
			querySQLStr.append(" AND u.job_title_id IN :filteredJobTitleIds");
		}

		if (StringUtils.isNotBlank(mapCondition.get(FILTERED_FROM_LAST_LOGIN)) && StringUtils.isNotBlank(mapCondition.get(FILTERED_TO_LAST_LOGIN))) {
			querySQLStr.append(" AND DATE_FORMAT(last_login_date_time, '%Y-%m-%d %H:%i:%s') BETWEEN DATE_FORMAT(:filteredFromLastLogin, '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(:filteredToLastLogin, '%Y-%m-%d %H:%i:%s')");
		} else if (StringUtils.isNotBlank(mapCondition.get(FILTERED_FROM_LAST_LOGIN))) {
			querySQLStr.append(" AND DATE_FORMAT(last_login_date_time, '%Y-%m-%d %H:%i:%s') >= DATE_FORMAT(:filteredFromLastLogin, '%Y-%m-%d %H:%i:%s')");
		} else if (StringUtils.isNotBlank(mapCondition.get(FILTERED_TO_LAST_LOGIN))) {
			querySQLStr.append(" AND DATE_FORMAT(last_login_date_time, '%Y-%m-%d %H:%i:%s') <= DATE_FORMAT(:filteredToLastLogin, '%Y-%m-%d %H:%i:%s')");
		}
		return querySQLStr;
	}
}

