package com.thinkhr.external.api.services.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.thinkhr.external.api.ApplicationConstants;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;

/**
 * To keep some common util methods 
 * 
 * @author Ajay
 * @since 2017-11-22
 *
 */
public class CommonUtil {

    /**
     * This will return current date and time in UTC
     * 
     * @return
     */
    public static String getTodayInUTC() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(ApplicationConstants.VALID_FORMAT_YYYY_MM_DD);
        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        return format.format(utcDateTime);
    }
    	
    /**
     * Create LearnCompany(mapped to thinkhr_learn.mdl_company) instance  
     * by mapping fields from Company(mapped to thinkhr_portal.clients) instance
     * TODO: code to map company object to learncompany object
     * 
     * @param company
     * @return
     */
    public static LearnCompany getLearnCompanyFromCompany(Company company) {
        LearnCompany learnCompany = createLearnCompany();

        learnCompany.setCompanyId(company.getCompanyId());
        return learnCompany;
    }
    
    
    // TODO : move this to APITestDataUtil
    public static LearnCompany createLearnCompany(String companyName, String companyKey, Long timeCreated,
            Long timeModified, Long createdBy) {

        LearnCompany learnCompany = new LearnCompany();
        learnCompany.setCompanyName(companyName);
        learnCompany.setCompanyKey(companyKey);
        learnCompany.setTimeCreated(timeCreated);
        learnCompany.setTimeModified(timeModified);
        learnCompany.setCreatedBy(createdBy);
        return learnCompany;
    }
    
    // TODO : move this to APITestDataUtil
    public static LearnCompany createLearnCompany() {
        return createLearnCompany("Pepcus" , "Key" , 0012121L , 232312312L   , 12345L );
    }
	
}
