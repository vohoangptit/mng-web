package com.nera.nms.dto;

import lombok.Data;

@Data
public class Meta {

	private Integer page = 0; // numberPage
	private Integer pages = 0; // total Page
	private Long total = 0l; // total Record
	private Integer perpage = 0; // page size
	private String field = "id"; // search field
	private String sort = "asc"; // sort type
}
