/**
 * 
 */
package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;

/**
 * @author Martin Do
 *
 */
@JsonRootName("meta")
@Data
public class PaginationDto {

    @JsonProperty(value="perpage", defaultValue = "10")
    private String perPage;
    
    @JsonProperty(value="page", defaultValue = "0")
    private String page;
    
    @JsonProperty(value="pages", defaultValue = "0")
    private String pages;
    
    @JsonProperty(value="field", defaultValue = "userName")
    private String sortBy;
    
    @JsonProperty(value="sort", defaultValue = "ASC")
    private String orderBy;
    
    @JsonProperty(value="generalSearch", defaultValue = "")
    private String searchText;
    
}
