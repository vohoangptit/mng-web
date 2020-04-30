package com.nera.nms.constants;

public class UserConstants {

    private UserConstants(){}

    /**
     * Table many to many link user and group name
     */
    public static final String REFERENCE_GROUP_USER = "user_group_user";

    /**
     * Table many to many link user and group name
     */
    public static final String REFERENCE_GROUP_ACCESS_PERMISSION = "user_access_permission_user_group";
    
    public static final int INVALID_LOGIN_MAX = 5;
    
    public static final String BLOCKED = "Blocked";
    
    public static final String ACTIVE = "Active";
    
    public static final String INACTIVE = "Inactive";
    

    /**
     * User login Description
     */
    public static final String USER_LOGIN_DESCRIPTION = "user login";

    /**
     * ACCOUT HAS LOCK MESSAGE 
     */
    public static final String LOCK_ACCOUNT_MESSAGE = "Your account has been locked due to repeated failed sign in attempts.";
    /**
     * User logout Description
     */
    public static final String USER_LOGOUT_DESCRIPTION = "user logout";
    
    /**
     * Is login
     */
    public static final boolean IS_LOGGIN = true;
    
    /**
     * Is authentication
     */
    public static final boolean IS_AUTHENTICAION = true;

}
