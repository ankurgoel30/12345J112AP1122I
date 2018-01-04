package com.thinkhr.external.api.services.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.hashids.Hashids;

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

    /**
     * Get tempID column value for Company entity.
     * 
     * @return
     */
    public static String getTempId() {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTime().getTime();
        return String.valueOf(currentTime);
    }

    /**
     * 
     * @return
     */
    public static Long getNowInMiliseconds() {
        Date now = new Date();
        return now.getTime();
    }
    	
    
    /**
     * @param value
     */
    public static String getHashedValue(Integer value) {
        Hashids hashids = new Hashids(ApplicationConstants.HASH_KEY);
        return hashids.encode(value);
    }
	
}
