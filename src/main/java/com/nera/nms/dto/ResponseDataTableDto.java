/**
 * 
 */
package com.nera.nms.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Martin Do
 * @param <T>
 *
 */
@Getter
@Builder
public class ResponseDataTableDto<T> {

    MetaDto meta;
    private List<T> data;
}
