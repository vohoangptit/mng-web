package com.nera.nms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserDto {

    @JsonProperty("userId")
    private String id;
}
