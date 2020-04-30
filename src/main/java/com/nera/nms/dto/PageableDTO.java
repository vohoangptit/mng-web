package com.nera.nms.dto;

import java.util.List;

import lombok.Data;

@Data
public class PageableDTO {

    private List<?> data;
    
    private Meta meta;
}
