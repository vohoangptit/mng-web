/**
 * 
 */
package com.nera.nms.components;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
/**
 * @author Martin Do
 *
 */
@Component
public class PageableComponent {

    /**
     * setting Pageable by parameter
     * @param page
     * @param pageSize
     * @param sortBy
     * @param orderBy
     * @return
     */
    public Pageable getPageable(String page, String pageSize, String sortBy, String orderBy) {
        int pageNumber = 0;

        if(!StringUtils.isBlank(page)) {
            pageNumber = Integer.parseInt(page);
        }

        if(pageNumber > 0) {
            pageNumber--;
        }

        return PageRequest.of(pageNumber, Integer.parseInt(pageSize), getSort(orderBy, sortBy));
    }

    /**
     * get sort for spring Pageable with orderBy
     * @param orderBy
     * @param sortBy
     * @return
     */
    public Sort getSort(String orderBy, String sortBy) {
        return new Sort(getDerection(orderBy), sortBy);
    }

    /**
     * get Direction for spring Pageable
     * @param orderBy
     * @return
     */
    public Direction getDerection(String orderBy) {
        return Sort.Direction.fromString(orderBy);
    }
}
