package com.nera.nms.constants;

import java.util.HashSet;
import java.util.Set;


public class JobExeConstants {

    private JobExeConstants(){}

    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String HEADER_ANSIBLE = "---"+NEW_LINE;
    public static final String MAINTITLE = "- name: ${title}"+NEW_LINE;
    public static final String MAINCONTENT =   "  hosts: ${host}"+NEW_LINE+"  roles:"+NEW_LINE+"    - Juniper.junos"+NEW_LINE+"    - ${role}"+NEW_LINE+"  connection: local";

    public static final String HEADER_HOST = "[${host}]"+NEW_LINE;
    public static final String HOST_CONTENT = "${hostname}    ansible_ssh_host=${address_ip} ansible_ssh_port=${port} ansible_ssh_user=${user} ansible_ssh_pass=${pass}"+NEW_LINE;

    public static final String DEFAULT_CONTENT = "${variable}: ${value}"+NEW_LINE;
    public static final String FOLDER = "${foldername}";
    public static final String PATH_1 = "inventories/job";
    public static final String PATH_2 = "${role}/defaults/";
    public static final String PATH_3 = "${role}/tasks/";
    public static final String PATH_4 = "${role}/files/";
    public static final String PATH_HOST = "hosts";
    public static final String PATH_MAIN = "${role}.yml";
    public static final String SCP_COMD = "pscp -scp -i C:/Nera/remote.ppk -r ";
    public static final  String SCP_TARGET = "remote@192.168.50.214:/tmp/nera/";
    private static final Set<String> JOB_PLANNING_STATUS = new HashSet<>();
    static {
        JOB_PLANNING_STATUS.add(HEADER_ANSIBLE);
        JOB_PLANNING_STATUS.add(MAINTITLE);
        JOB_PLANNING_STATUS.add(MAINCONTENT);
        JOB_PLANNING_STATUS.add(HEADER_HOST);
        JOB_PLANNING_STATUS.add(HOST_CONTENT);
        JOB_PLANNING_STATUS.add(DEFAULT_CONTENT);
        JOB_PLANNING_STATUS.add(CommonConstants.PATH_DELIMITER+PATH_1);
        JOB_PLANNING_STATUS.add(CommonConstants.PATH_DELIMITER+PATH_2);
        JOB_PLANNING_STATUS.add(CommonConstants.PATH_DELIMITER+PATH_3);
        JOB_PLANNING_STATUS.add(CommonConstants.PATH_DELIMITER+PATH_4);
        JOB_PLANNING_STATUS.add(CommonConstants.PATH_DELIMITER+PATH_MAIN);
        JOB_PLANNING_STATUS.add(SCP_COMD);
        JOB_PLANNING_STATUS.add(SCP_TARGET);
    }

    public static Set<String> getJobPlanningStatus() {
        return JOB_PLANNING_STATUS;
    }
}
