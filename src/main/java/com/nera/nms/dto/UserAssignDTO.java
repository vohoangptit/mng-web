package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserAssignDTO {

    @JsonProperty("id")
    private String id;
    
    @JsonProperty("fullName")
    private String fullName;
    
    @JsonProperty("email")
    private String email;
}
