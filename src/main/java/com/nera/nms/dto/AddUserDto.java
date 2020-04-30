package com.nera.nms.dto;

import lombok.Data;

@Data
public class AddUserDto {

    private String email;
    private String fullName;
    private long selectedGroupsIds;
    private long department;
    private long jobTitle;
    private String mobileNumber;
    private boolean status;
}
