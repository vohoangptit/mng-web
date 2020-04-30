package com.nera.nms.utils;

import javax.servlet.http.HttpServletRequest;

public class UrlBuilder {

	private UrlBuilder() {
	}

	public static String getServerUrl(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}
}
