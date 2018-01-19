package com.thinkhr.external.api.model;

import java.lang.reflect.Field;
import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.SEMI_COLON_SEPARATOR;

import lombok.Data;

/**
 * Model class to support JSON bulk company upload
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-12
 *
 */

@Data
public class CompanyJsonBulk {
	
	private String client_name;
	private String display_name;
	private String phone;
	private String address;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String industry;
	private int company_size;
	private String producer;
	private int business_id;
	private int branch_id;
	private int client_id;
	private String client_type;
	
	@Override
	public String toString(){
		Field[] allFields = this.getClass().getDeclaredFields();
		
		StringBuilder companyModelHeader = new StringBuilder();
		StringBuilder companyModelValues = new StringBuilder();
		
		for (Field field : allFields) {
			companyModelHeader.append(COMMA_SEPARATOR+field.getName().toUpperCase());
			field.setAccessible(true);  
			try {
				companyModelValues.append(COMMA_SEPARATOR+field.get(this).toString());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		return companyModelHeader.append(SEMI_COLON_SEPARATOR+companyModelValues.toString().substring(1)).toString().substring(1);

	}

}
