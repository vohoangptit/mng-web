package com.nera.nms.utils;

import com.nera.nms.dto.UserProfileDto;
import org.apache.commons.lang3.StringUtils;

public final class UserConvertUtil {

    private UserConvertUtil(){}

    public static UserProfileDto convertUserOptionToDto(Object user) {
        if(user == null) {
            return null;
        }
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setFullName(((Object [])user)[0]!= null ? ((Object [])user)[0].toString() : StringUtils.EMPTY);
        userProfileDto.setEmail(((Object [])user)[1] != null ? ((Object [])user)[1].toString() : StringUtils.EMPTY);
        userProfileDto.setMobileNo(((Object [])user)[2] != null ? ((Object [])user)[2].toString() : StringUtils.EMPTY);
        userProfileDto.setGroups(((Object [])user)[3] != null? ((Object [])user)[3].toString() : StringUtils.EMPTY);
        userProfileDto.setDepartment(((Object [])user)[4] != null ? ((Object [])user)[4].toString() : StringUtils.EMPTY);
        userProfileDto.setJobTitle(((Object [])user)[5] != null ? ((Object [])user)[5].toString() : StringUtils.EMPTY);
        userProfileDto.setImageUrl(((Object [])user)[6] != null ? ((Object [])user)[6].toString(): StringUtils.EMPTY);

        return userProfileDto;
    }
}
