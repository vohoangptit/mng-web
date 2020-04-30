package com.nera.nms.constants;

import java.awt.image.BufferedImage;
public class WorkflowConstants {

	private WorkflowConstants(){}
	/**
	 * Contains an empty image.
	 */
	public static final BufferedImage EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

	/**
	 * Maximum size (in bytes) for request payloads. Default is 10485760 (10MB).
	 */
	public static final int MAX_REQUEST_SIZE = 10485760;

	/**
	 * Maximum area for exports. Default is 10000x10000px.
	 */
	public static final int MAX_AREA = 10000 * 10000;

}
