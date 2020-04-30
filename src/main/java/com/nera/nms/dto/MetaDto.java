/**
 * 
 */
package com.nera.nms.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Martin Do
 *
 */
@Getter
@Builder
public class MetaDto {

    private String page;
    private String pages;
    private String perpage;
    private long total;
    private String sort;
}
