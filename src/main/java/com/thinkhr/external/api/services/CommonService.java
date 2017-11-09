package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
     * Get Pageable instance
     * @param offset
     * @param limit
     * @param sortedBy
     * @return
     */
    public Pageable getPageable(Integer offset,Integer limit,String sortedBy) {
    	OffsetPageRequest pageable = null;
    	offset = offset == null ? DEFAULT_OFFSET : offset;
    	limit = limit == null ? DEFAULT_LIMIT : limit;
    	sortedBy = StringUtils.isBlank(sortedBy) ? getDefaultSortField() : sortedBy;

    	Sort.Direction sortDirection = getSortDirection(sortedBy);
    	
		sortedBy = extractSortDirection(sortedBy, sortDirection); // Extracted out + or - character from sortBy string

		Sort sort = new Sort(sortDirection,sortedBy);
		
    	pageable = new OffsetPageRequest(offset/limit, limit, sort);
    	pageable.setOffset(offset);
    	return pageable;
    }
    
    /**
     * It will extract + or - character those stands for sort direction from column field name
     * @param sortedBy
     * @param sortDirection
     * @return
     */
    private String extractSortDirection(String sortedBy, Sort.Direction sortDirection) {
    	String directionIndicator = sortedBy.substring(0,1);
		if(directionIndicator.equals(ASCENDING) || directionIndicator.equals(DESENDING) ) {
			sortedBy = sortedBy.substring(1);
		} 
		
		return sortedBy;
	}

	/**
     * Get the first character out from sortedBy value. 
     * like +companyName
     * @param sortedBy
     * @return
     */
    private Direction getSortDirection(String sortedBy) {
    	String sortDirection =  sortedBy.substring(0,1);
    	return DESENDING.equalsIgnoreCase(sortDirection) ? Direction.DESC : Direction.ASC;
	}

	public String getDefaultSortField()  {
    	return null;
    }
   
 
}
