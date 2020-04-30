package com.nera.nms.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.nera.nms.dto.GroupDTO;
import com.nera.nms.dto.Meta;
import com.nera.nms.dto.PageableDTO;

public final class PageableUtil {

	private PageableUtil(){}

	private static Logger logger = LoggerFactory.getLogger(PageableUtil.class);

	/**
	 * <p>mapper data query from database to object return for UI design</p>
	 * @param page
	 * @param pageable
	 * @return  {@link PageableDTO}
	 */
	@SuppressWarnings("unchecked")
	public static PageableDTO pageableMapperJson(Page<?> page, Pageable pageable) {
		PageableDTO data = new PageableDTO();
		try {
			//row number for No
	        AtomicLong numIncrement= new AtomicLong(1);
	        List<GroupDTO> datas; 
	        datas = (List<GroupDTO>)page.getContent();
	        datas.forEach(object->object.setNo(numIncrement.getAndIncrement()+page.getNumber()*10));
	        data.setData(datas);
			Meta meta = new Meta();
			meta.setPage(page.getNumber()+1);
			meta.setPages(page.getTotalPages());
			meta.setPerpage(page.getSize());
			meta.setTotal(page.getTotalElements());
			if (pageable.getSort().toString().equals("UNSORTED")) {
				meta.setSort("");
				meta.setField("");
			} else {
				String[] sort = pageable.getSort().toString().split("\\:");
				meta.setSort(sort[1].trim().toLowerCase());
				meta.setField(sort[0]);
			}
			data.setMeta(meta);
		} catch (Exception e) {
			logger.error("Exception: PageableUtil.pageableMapperJson ", e);
		}
		return data;
	}
	
	/**
	 * <p>This is function create pageable from client</p>
	 * @param page
	 * @param perPage
	 * @param sortBy
	 * @param orderBy
	 * @return
	 */
	public static Pageable createPageable(int page, int perPage, String sortBy, String orderBy) {
		Direction direction = Sort.Direction.fromString(orderBy);
		Sort sort = new Sort(direction, sortBy);
		return PageRequest.of(page, perPage, sort);
	}

	public static PageableDTO pageableMapper(Page<?> page, Pageable pageable, List<?> content) {
		PageableDTO data = new PageableDTO();
		try {
	        data.setData(content);
			Meta meta = new Meta();
			meta.setPage(page.getNumber()+1);
			meta.setPages(page.getTotalPages());
			meta.setPerpage(page.getSize());
			meta.setTotal(page.getTotalElements());
			if (pageable.getSort().toString().equals("UNSORTED")) {
				meta.setSort("");
				meta.setField("");
			} else {
				String[] sort = pageable.getSort().toString().split("\\:");
				meta.setSort(sort[1].trim().toLowerCase());
				meta.setField(sort[0]);
			}
			data.setMeta(meta);
		} catch (Exception e) {
			logger.error("Exception: PageableUtil.pageableMapper ", e);
		}
		return data;
	}

	public static PageableDTO pageableMapperProperties(Page<?> page, Pageable pageable, List<?> content) {
		PageableDTO data = new PageableDTO();
		try {
			data.setData(content);
			Meta meta = new Meta();
			meta.setPage(page.getNumber()+1);
			meta.setPages(page.getTotalPages());
			meta.setPerpage(page.getSize());
			meta.setTotal(page.getTotalElements());
			if (pageable.getSort().toString().equals("UNSORTED")) {
				meta.setSort("");
				meta.setField("");
			} else {
				String[] sort = pageable.getSort().toString().split("\\:");
				meta.setSort(sort[1].trim().toLowerCase());
				meta.setField(sort[0]);
			}
			data.setMeta(meta);
		} catch (Exception e) {
			logger.error("Exception: PageableUtil.pageableMapper ", e);
		}
		return data;
	}
}
