package com.nera.nms.repositories.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.nera.nms.constants.CommonConstants;
import org.springframework.stereotype.Repository;

import com.nera.nms.models.FileManagement;

@Repository
public class FileManagementDaoImpl {
	@PersistenceContext
	EntityManager em;
	
	public long findFilesWithQuery(String page, String pageSize, String sortBy, String orderBy, String searchText, final List <FileManagement> fileList) {
		
		int pageInt = Integer.parseInt(page) - 1;
    	int pageSizeInt = Integer.parseInt(pageSize);

    	pageInt = pageInt * pageSizeInt;
    	
    	em.getEntityManagerFactory();

    	StringBuilder querySQLStr = new StringBuilder("");
    	
    	querySQLStr.append(" WHERE fm.is_deleted = 'N' ");
    	
    	if (!searchText.isEmpty()) {
    		if (searchText.toLowerCase().contains("active")) {
        		querySQLStr.append(" AND fm.search_string LIKE :text ");
        	} else {	
        		querySQLStr.append(" AND fm.search_string LIKE '%' :text '%'");
        	}
    	}
    	
    	StringBuilder countQueryStr = new StringBuilder("SELECT COUNT(fm.id) FROM file_management fm ").append(querySQLStr);
    	
    	StringBuilder queryStr = new StringBuilder("SELECT * FROM file_management fm").append(querySQLStr);

    	String sortByColName = "";
    	switch(sortBy) {
    	case "name":
    		sortByColName = "name";
    		break;
    	case "filename":
    		sortByColName = "filename";
    		break;
    	case "createdBy":
    		sortByColName = "created_by";
    		break;
    	case "createdByDate":
    		sortByColName = "create_date";
    		break;
    	case CommonConstants.STATUS:
    		sortByColName = "is_active";
    		break;
    	default:
    		sortByColName = "create_date";
    	}
    	
    	queryStr.append(" ORDER BY " + sortByColName + " ");
    	if (orderBy.equalsIgnoreCase("DESC")) {
    		queryStr.append("DESC");
    	} else {
    		queryStr.append("ASC");
    	}
    	javax.persistence.Query querySQL = em.createNativeQuery(queryStr.toString(), FileManagement.class);
    	javax.persistence.Query countQuerySQL = em.createNativeQuery(countQueryStr.toString());
    	
    	if (!searchText.isEmpty()) {
	    	if (searchText.toLowerCase().contains("active")) {
	    		querySQL.setParameter("text", "% " + searchText + " %"); //note: need to have space after and before %
	    		countQuerySQL.setParameter("text", "% " + searchText + " %"); 
	    	} else {
	    		querySQL.setParameter("text", searchText);
	    		countQuerySQL.setParameter("text", searchText);
	    	}
    	}
    	
    	querySQL.setFirstResult(pageInt);
    	querySQL.setMaxResults(pageSizeInt);

    	fileList.addAll(querySQL.getResultList());
    	return Long.parseLong(countQuerySQL.getSingleResult().toString());
	}
}

