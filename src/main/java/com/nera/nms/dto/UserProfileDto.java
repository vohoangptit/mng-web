/**
 * 
 */
package com.nera.nms.dto;

import com.nera.nms.constants.CommonConstants;
import lombok.Data;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Data
public class UserProfileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fullName;
    
    private String email;
    
    private String mobileNo;

    private String groups;
    
    private String department;
    
    private String jobTitle;
    
    private String imageUrl;

    private String permission;

    public void addToSession(HttpSession session) {
        session.setAttribute(CommonConstants.USER_PROFILE, this);
    }
}
