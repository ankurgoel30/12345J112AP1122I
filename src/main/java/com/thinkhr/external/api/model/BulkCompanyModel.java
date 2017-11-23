/**
 * 
 */
package com.thinkhr.external.api.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class BulkCompanyModel {
	
	public String customHeaders;
	public List<CompanyJSONModel> companies;


	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class CompanyJSONModel {
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
	public String custom1;
	public String custom2;
	public String custom3;
	public String custom4;
	
	
	/**
	 * Converts object values into comma separated values
	 * 
	 * @return
	 */
	public String toCsvRow() {
	    return Stream.of(clientName, displayName, phone, address, address2, city, 
				  state, zip, industry, companySize, producer, custom1, 
				  custom2, custom3, custom4)
	            .map(value -> value.replaceAll("\"", "\"\""))
	            .map(value -> Stream.of("\"", ",").anyMatch(value::contains) ? "\"" + value + "\"" : value)
	            .collect(Collectors.joining(","));
	}
  }
}