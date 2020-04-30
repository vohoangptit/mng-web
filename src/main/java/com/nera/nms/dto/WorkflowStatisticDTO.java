package com.nera.nms.dto;

import lombok.Data;

@Data
public class WorkflowStatisticDTO {

    private long id;

    private String name;

    private int usage;

    private int secondEve;

    private String timeView;
}