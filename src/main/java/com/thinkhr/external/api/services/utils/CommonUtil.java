package com.thinkhr.external.api.services.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
    //TODO - Move this to some util file. 
    public static String getTodayInUTC() {
    	 TimeZone timeZone = TimeZone.getTimeZone("UTC");
    	 Calendar calendar = Calendar.getInstance(timeZone);
    	 calendar.setTime(new Date());
    	 DateFormat simpleDateFormat = new SimpleDateFormat(ApplicationConstants.VALID_FORMAT_YYYY_MM_DD);
    	 simpleDateFormat.setTimeZone(timeZone);
		return simpleDateFormat.format(calendar.getTime());
    }
    	
	
}
