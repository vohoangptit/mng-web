/**
 * 
 */
package com.nera.nms.dto;

import lombok.Data;

/**
 * @author Martin Do
 *
 */
@Data
public class SystemSettingDto {

    private String email = "";

    private String smtpHost = "";

    private int smtpPort = 0;

    private boolean sbSmtpSecurity = false;

    private String username = "";

    private String password ="";

    private int jobPlanningConfig = 0;
}
