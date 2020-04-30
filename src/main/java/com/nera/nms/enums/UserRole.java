/**
 * 
 */
package com.nera.nms.enums;

/**
 * @author Martin Do
 *
 */
public enum UserRole {

    ADMINISTRATOR("Administrator"),
    TEAM_LEADER("Team Leader"),
    ENGINEER("Engineer");
    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String role() {
        return role;
    }
}
