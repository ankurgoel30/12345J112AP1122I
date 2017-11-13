package com.thinkhr.external.api.services;

import org.springframework.stereotype.Service;

import lombok.Data;

/**
 * Common Service to hold all general operations
 * 
 * @author Surabhi Bhawsar
 * @Since 2017-11-04
 *
 */
@Service
@Data
public class CommonService {

	/**
	 * @return
	 */
	public String getDefaultSortField()  {
    	return null;
    }
	
 
}
