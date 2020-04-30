package com.nera.nms.dto;

import lombok.Data;

@Data
public class ResultDTO {

    private int code;
    
    private String mess;
    
    private String detail;
    
    private String fieldName;

    private long id;
}
