package com.thinkhr.external.api.services;

import java.lang.reflect.Field;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
