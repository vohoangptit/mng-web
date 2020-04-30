package com.nera.nms.repositories.impl;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.SystemAuditLogDto;
import com.nera.nms.models.SystemAuditLog;
import com.nera.nms.utils.BEDateUtils;
import com.nera.nms.utils.BeanConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class AuditLogDaoImpl {
	@PersistenceContext
	EntityManager em;

	private static final Logger logger = LoggerFactory.getLogger(AuditLogDaoImpl.class);

	private static final String FILTERED_FROM_ACTION_DATE = "filteredFromActionDate";

	private static final String FILTERED_TO_ACTION_DATE = "filteredToActionDate";

	private static final String START_HOURS = " 00:00:00";

	private static final String END_HOURS = " 23:59:59";
	
	public List<SystemAuditLogDto> findAuditLogWithQuery(String page, String pageSize, String sortBy, String orderBy, String searchText,
										String filteredFromActionDate, String filteredToActionDate) {
		int pageInt = Integer.parseInt(page) - 1;
    	int pageSizeInt = Integer.parseInt(pageSize);

    	pageInt = pageInt * pageSizeInt;

    	em.getEntityManagerFactory();

		StringBuilder querySQLStr = getStringBuilder(filteredFromActionDate, searchText, filteredToActionDate);

    	StringBuilder queryStr = new StringBuilder("SELECT * FROM system_audit_log l").append(querySQLStr);

    	String sortByColName = "";
    	switch(sortBy) {
			case "userName":
				sortByColName = "user_name";
				break;
			case "userGroupName":
				sortByColName = "user_group_name";
				break;
			case "description":
				sortByColName = "description";
				break;
			case "details":
				sortByColName = "details";
				break;
			case CommonConstants.STATUS:
				sortByColName = CommonConstants.STATUS;
				break;
			case "actionDate":
				sortByColName = "action_date";
				break;
			default:
    	}
    	
    	queryStr.append(" ORDER BY " + sortByColName + " ");
    	if (orderBy.equalsIgnoreCase("DESC")) {
    		queryStr.append("DESC");
    	} else {
    		queryStr.append("ASC");
    	}
    	javax.persistence.Query querySQL = em.createNativeQuery(queryStr.toString(), SystemAuditLog.class);

    	if (StringUtils.isNotBlank(searchText)) {
			querySQL.setParameter("text", searchText);
    	}

    	try {
			if (StringUtils.isNotBlank(filteredFromActionDate) && StringUtils.isNotBlank(filteredToActionDate)) {
				String newFromActionDate = filteredFromActionDate + START_HOURS;
				String newToActionDate = filteredToActionDate + END_HOURS;

				querySQL.setParameter(FILTERED_FROM_ACTION_DATE, BEDateUtils.formatDate(newFromActionDate, CommonConstants.DATE_TIME_SEC_FORMAT));
				querySQL.setParameter(FILTERED_TO_ACTION_DATE, BEDateUtils.formatDate(newToActionDate, CommonConstants.DATE_TIME_SEC_FORMAT));

			} else if (StringUtils.isNotBlank(filteredFromActionDate)) {
				String newFromActionDate = filteredFromActionDate + START_HOURS;

				querySQL.setParameter(FILTERED_FROM_ACTION_DATE, BEDateUtils.formatDate(newFromActionDate, CommonConstants.DATE_TIME_SEC_FORMAT));
			} else if (StringUtils.isNotBlank(filteredToActionDate)) {
				String newToActionDate = filteredToActionDate + END_HOURS;

				querySQL.setParameter(FILTERED_TO_ACTION_DATE, BEDateUtils.formatDate(newToActionDate, CommonConstants.DATE_TIME_SEC_FORMAT));
			}
		} catch (ParseException e) {
			logger.error("AuditLogDaoImpl.findAuditLogWithQuery : ", e);
		}
    	querySQL.setFirstResult(pageInt);
    	querySQL.setMaxResults(pageSizeInt);

    	List<SystemAuditLog> logList = new ArrayList <>();
    	logList.addAll(querySQL.getResultList());
    	return BeanConvertUtils.copyList(logList, SystemAuditLogDto.class);
	}

	public long countAuditByQuery(String searchText, String filteredFromActionDate, String filteredToActionDate) {
		em.getEntityManagerFactory();
		StringBuilder querySQLStr = getStringBuilder(filteredFromActionDate, searchText, filteredToActionDate);

		String countQueryStr = "SELECT COUNT(l.id) FROM system_audit_log l " + querySQLStr;
		javax.persistence.Query countQuerySQL = em.createNativeQuery(countQueryStr);

		if (StringUtils.isNotBlank(searchText)) {
			countQuerySQL.setParameter("text", searchText);
		}

		try {
			if (StringUtils.isNotBlank(filteredFromActionDate) && StringUtils.isNotBlank(filteredToActionDate)) {
				String newFromActionDate = filteredFromActionDate + START_HOURS;
				String newToActionDate = filteredToActionDate + END_HOURS;
				countQuerySQL.setParameter(FILTERED_FROM_ACTION_DATE, BEDateUtils.formatDate(newFromActionDate, CommonConstants.DATE_TIME_SEC_FORMAT));
				countQuerySQL.setParameter(FILTERED_TO_ACTION_DATE, BEDateUtils.formatDate(newToActionDate, CommonConstants.DATE_TIME_SEC_FORMAT));
			} else if (StringUtils.isNotBlank(filteredFromActionDate)) {
				String newFromActionDate = filteredFromActionDate + START_HOURS;
				countQuerySQL.setParameter(FILTERED_FROM_ACTION_DATE, BEDateUtils.formatDate(newFromActionDate, CommonConstants.DATE_TIME_SEC_FORMAT));
			} else if (StringUtils.isNotBlank(filteredToActionDate)) {
				String newToActionDate = filteredToActionDate + END_HOURS;
				countQuerySQL.setParameter(FILTERED_TO_ACTION_DATE, BEDateUtils.formatDate(newToActionDate, CommonConstants.DATE_TIME_SEC_FORMAT));
			}

		} catch (ParseException e) {
			logger.error("AuditLogDaoImpl.countAuditByQuery : ", e);
		}
		return Long.parseLong(countQuerySQL.getSingleResult().toString());
	}

	private StringBuilder getStringBuilder(String filteredFromActionDate, String searchText, String filteredToActionDate) {
		StringBuilder querySQLStr = new StringBuilder("");

		querySQLStr.append(" WHERE user_name IS NOT NULL ");

		if (StringUtils.isNotBlank(searchText)) {
			querySQLStr.append(" AND l.search_string LIKE '%' :text '%'");
		}

		if (StringUtils.isNotBlank(filteredFromActionDate) && StringUtils.isNotBlank(filteredToActionDate)) {
			querySQLStr.append(" AND DATE_FORMAT(action_date, '%Y-%m-%d %H:%i:%s') BETWEEN DATE_FORMAT(:filteredFromActionDate, '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(:filteredToActionDate, '%Y-%m-%d %H:%i:%s')");
		} else if (StringUtils.isNotBlank(filteredFromActionDate)) {
			querySQLStr.append(" AND DATE_FORMAT(action_date, '%Y-%m-%d %H:%i:%s') BETWEEN DATE_FORMAT(:filteredFromActionDate, '%Y-%m-%d %H:%i:%s')  AND NOW()");
		} else if (StringUtils.isNotBlank(filteredToActionDate)) {
			querySQLStr.append(" AND DATE_FORMAT(action_date, '%Y-%m-%d %H:%i:%s') BETWEEN DATE_FORMAT(:filteredToActionDate, '%Y-%m-%d %H:%i:%s')  AND NOW()");
		}
		return querySQLStr;
	}
}

