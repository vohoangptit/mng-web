package com.nera.nms.dto;

import lombok.Data;

/**
 * @author Phuong
 */
@Data
public class JobInputDTO {

	private long no;

	private long id;

	private int type;
	
	private String variable;
	
	private String value;

	private int del;
}
