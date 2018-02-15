package com.thinkhr.external.api.interceptors;

import static com.thinkhr.external.api.ApplicationConstants.NEW_LINE;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Class for evaluating/watching time for Bulk Upload APIs 
 * 
 * Value of "bulk.time.watcher.enabled" property should be set to true for enabling this watcher  
 * 
 * @author Surabhi Bhawsar
 */

@Component
@Aspect
@ConditionalOnProperty(value = "bulk.time.watcher.enabled")
public class BulkTimeWatcher {
    
    private Logger logger = LoggerFactory.getLogger(BulkTimeWatcher.class);
 
    @Pointcut("execution(* com.thinkhr.external.api.services.UserService.bulkUpload(..))")
    public void userBulkUploadMethod() {
    };


    @Pointcut("execution(* com.thinkhr.external.api.services.UserService.sendMail(..))")
    public void sendEmailPointcut() {
    };

    StopWatch bulkUploadComplete = null;
    StopWatch beforeLoop = null;
    StopWatch completeLoopTime = null;
    StopWatch populateAndSavetoDb = null;
    StopWatch passwordEncrypt = null;
    StopWatch generateUserName = null;
    StopWatch throneUserSave = null;
    StopWatch learnUserSave = null;
    StopWatch userInsertQuery = null;
    public static StopWatch sendEmailWatch = null;
 
    @Before("userBulkUploadMethod()")
    public void bulkUploadMethodEnter(JoinPoint jp) {
        initClocks();
        bulkUploadComplete.start();
    }

    @After("userBulkUploadMethod()")
    public void bulkUploadMethodExit(JoinPoint jp) {
        bulkUploadComplete.stop();
        logger.info(printBulkWatchReport());
    }


    @Before("sendEmailPointcut()")
    public void sendEmailBefore(JoinPoint jp) {
        logger.info("In sendEmail feature.....");
        sendEmailWatch.start();
    }

    @After("sendEmailPointcut()")
    public void sendEmailAfter(JoinPoint jp) {
        sendEmailWatch.stop();
    }

    private void initClocks() {
        bulkUploadComplete = new StopWatch();
        beforeLoop = new StopWatch();
        completeLoopTime = new StopWatch();
        populateAndSavetoDb = new StopWatch();
        passwordEncrypt = new StopWatch();
        generateUserName = new StopWatch();
        throneUserSave = new StopWatch();
        learnUserSave = new StopWatch();
        userInsertQuery = new StopWatch();
        sendEmailWatch = new StopWatch();
    }

    private String printBulkWatchReport() {
        StringBuffer strBuffer = new StringBuffer();

        return strBuffer.append("Bulk Upload Complete - " + bulkUploadComplete.getTotalTimeSeconds())
                .append(NEW_LINE)
                .append("Before Forloop - " + beforeLoop.getTotalTimeSeconds())
                .append(NEW_LINE)
                .append("Complete loop time - " + completeLoopTime.getTotalTimeSeconds())
                .append(NEW_LINE)
                .append("Time in populate and save to DB  - " + populateAndSavetoDb.getTotalTimeSeconds())
                .append(NEW_LINE)
                .append("Time in passsword Encrypt - " + passwordEncrypt.getTotalTimeSeconds())
                .append(NEW_LINE)
                .append("Time in generate username - " + generateUserName.getTotalTimeSeconds())
                .append(NEW_LINE)
                .append("Time in Throne Db - " + throneUserSave.getTotalTimeSeconds())
                .append(NEW_LINE)
                .append("Time in LearnDb - " + learnUserSave.getTotalTimeSeconds())
                .append(NEW_LINE)
                .append("Time in SendEmail - " + sendEmailWatch.getTotalTimeSeconds())
                .append(NEW_LINE)
                .append("Time in buildQuery - " + userInsertQuery.getTotalTimeMillis()).toString();
    }
}