/**
 * 
 */
package com.nera.nms.enums;

/**
 * @author Martin Do
 *
 */
public enum Sort {

    ASCENDING("ASC"),
    DESCENDING("DESC");

    private String sortBy;

    Sort(String sortBy) {
        this.sortBy = sortBy;
    }

    public String sortBy() {
        return sortBy;
    }
}