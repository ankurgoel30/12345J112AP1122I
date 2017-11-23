package com.thinkhr.external.api.services.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.thinkhr.external.api.ApplicationConstants;

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
    	
	
}
