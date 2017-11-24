/**
 * 
 */
package com.thinkhr.external.api.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

import com.thinkhr.external.api.ApplicationConstants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This model pojo is used to hold JSON data
 * for bulk upload of companies.
 * 
 * @author Ajay
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkCompanyModel {
	
	public String customHeaders;
	public List<CompanyJSONModel> companies;


	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CompanyJSONModel {
	public String clientName;
	public String displayName; 
	public String phone;
	public String address; 
	public String address2;
	public String city; 
	public String state;
	public String zip; 
	public String industry;
	public String companySize;
	public String producer;
	public String customHeader1;
	public String customHeader2;
	public String customHeader3;
	public String customHeader4;
	
	
	/**
	 * Converts object values into comma separated values
	 * 
	 * @return
	 */
	public String toCsvRow() {
	    return Stream.of(clientName, displayName, phone, address, address2, city, 
				  state, zip, industry, companySize, producer, customHeader1, 
				  customHeader2, customHeader3, customHeader4)
	            .map(value -> value.replaceAll("\"", "\"\""))
	            .map(value -> Stream.of("\"", ",").anyMatch(value::contains) ? "\"" + value + "\"" : value)
	            .collect(Collectors.joining(","));
	}
  }
    
	
	/**
     * Get refined headers for JSON data
     * 
     * @param customHeaders
     * @return
     */
	public String determineHeaders() {
       	String requiredHeaders =  StringUtils.join(ApplicationConstants.REQUIRED_HEADERS_COMPANY_CSV_IMPORT, ","); 
    	if (StringUtils.isEmpty(customHeaders)) {
    		return requiredHeaders;
    	} 
       	return requiredHeaders+","+StringUtils.deleteWhitespace(customHeaders);
    }    	
		
}