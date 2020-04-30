package com.nera.nms.utils;

import java.util.regex.Pattern;

public final class ValidationUtil {

	private ValidationUtil() {
	}

	private static final Pattern PATTERN = Pattern.compile(
	        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	/**
	 * @param ip
	 * @return
	 */
	public static boolean validate(final String ip) {
	    return PATTERN.matcher(ip).matches();
	}
}
