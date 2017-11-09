package com.thinkhr.external.api.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 * Extended Java Bean for PageRequest with additional attribute offset.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-09
 *
 */
public class OffsetPageRequest extends PageRequest {
	private static final long serialVersionUID = 1L;
	
	int offset;
	
	/**
	 * OffsetPageRequest constructor 
	 * @param page
	 * @param size
	 */
	public OffsetPageRequest(int page, int size) {
		super(page, size);
	}
	
	/**
	/**
	 * OffsetPageRequest constructor 
	 * @param page
	 * @param size
	 * @param direction
	 * @param properties
	 */
	public OffsetPageRequest(int page, int size, Direction direction, String... properties) {
		super(page, size, direction, properties);
	}

	/**
	/**
	 * OffsetPageRequest constructor 
	 * @param page
	 * @param size
	 * @param sort
	 */
	public OffsetPageRequest(int page, int size, Sort sort) {
		super(page, size, sort);
	}

	@Override
	public int getOffset() {
		return offset;
	}
	
	/**
	 * @param offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
}
