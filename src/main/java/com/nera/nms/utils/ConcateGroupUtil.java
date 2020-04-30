package com.nera.nms.utils;

import java.util.List;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.models.Group;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class ConcateGroupUtil {

	private ConcateGroupUtil() {
	}

	public static String concateGroupNames(List<Group> groupList) {
		if (CollectionUtils.isEmpty(groupList)) {
			return StringUtils.EMPTY;
		}
		StringBuilder groupStr = new StringBuilder();
        for (Group group: groupList) {
        	groupStr.append(group.getGroupName()).append(CommonConstants.COMMA);
        }
        return groupStr.toString().substring(0, groupStr.length() - 1);
	}
}
