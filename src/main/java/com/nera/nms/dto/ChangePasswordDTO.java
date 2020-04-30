package com.nera.nms.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {

    private String email;
    private String oldPassword;
    private String newPassword;
}
