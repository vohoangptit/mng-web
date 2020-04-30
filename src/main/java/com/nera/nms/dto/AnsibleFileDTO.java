package com.nera.nms.dto;

import lombok.Data;

import java.util.List;

/**
 * The type Ansible file dto.
 */
@Data
public class AnsibleFileDTO {
    private String title;

    private String host;

    private List<String> user;

    private List<String> pass;

    private String role;

    private String foldername;

    private List<String> port;

    private List<String> addressIps;

    private List<String> hostname;

    private List<String> variable;

    private List<String> value;

    private List<String> yml;

    private List<String> files;

    private List<JobPayloadDTO> jobPayloads;

    private List<String> resultPlaybooks;

    private String jobName;
}
