package com.thinkhr.external.api.interceptors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


@Component
@Aspect
@ConditionalOnProperty(value = "performanceAspect.enabled")
/**
 * Java class for tracking performance  of functions 
 * Make the value of performanceAspect.enabled property to true to enable this Aspect
 * 
 * @author Ajay Jain
 */
public class PerformanceAspect {
 
    @Pointcut("execution(* com.thinkhr.external.api.services.UserService.bulkUpload(..))")
    public void userBulkUploadMethod() {
    };

    @Pointcut("execution(* com.thinkhr.external.api.services.UserService.populateAndSaveToDB(..))")
    public void userPopulateAndSaveToDbMethod() {
    };

    @Pointcut("execution(* com.thinkhr.external.api.repositories.QueryBuilder.buildUserInsertQuery(..))")
    public void buildUserInsertQuery() {
    };

    StopWatch bulkUploadComplete = new StopWatch();
    StopWatch beforeLoop = new StopWatch();
    StopWatch completeLoopTime = new StopWatch();
    StopWatch populateAndSavetoDb = new StopWatch();
    StopWatch passwordEncrypt = new StopWatch();
    StopWatch generateUserName = new StopWatch();
    StopWatch throneUserSave = new StopWatch();
    StopWatch learnUserSave = new StopWatch();
    StopWatch userInsertQuery = new StopWatch();
 
    @Before("userBulkUploadMethod()")
    public void bulkUploadMethodEnter(JoinPoint jp) {
        initClocks();
        System.out.println("Hi");
        bulkUploadComplete.start();
    }

    @After("userBulkUploadMethod()")
    public void bulkUploadMethodExit(JoinPoint jp) {
        bulkUploadComplete.stop();
        printTimeTaken();
    }

    //    @Before("userPopulateAndSaveToDbMethod()")
    //    public void pAStDBEnter(JoinPoint jp) {
    //        populateAndSavetoDb.start();
    //    }
    //
    //    @After("userPopulateAndSaveToDbMethod()")
    //    public void pAStDBExit(JoinPoint jp) {
    //        populateAndSavetoDb.stop();
    //    }

    @Before("buildUserInsertQuery()")
    public void bUIQEnter(JoinPoint jp) {
        userInsertQuery.start();
    }

    @After("buildUserInsertQuery()")
    public void bUIQExit(JoinPoint jp) {
        userInsertQuery.stop();
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
    }

    private void printTimeTaken() {
        System.out.println("Bulk Upload Complete - " + bulkUploadComplete.getTotalTimeSeconds());
        System.out.println("Before Forloop - " + beforeLoop.getTotalTimeSeconds());
        System.out.println("Complete loop time - " + completeLoopTime.getTotalTimeSeconds());
        System.out.println("Time in populate and save to DB  - " + populateAndSavetoDb.getTotalTimeSeconds());
        System.out.println("Time in passsword Encrypt - " + passwordEncrypt.getTotalTimeSeconds());
        System.out.println("Time in generate username - " + generateUserName.getTotalTimeSeconds());
        System.out.println("Time in Throne Db - " + throneUserSave.getTotalTimeSeconds());
        System.out.println("Time in LearnDb - " + learnUserSave.getTotalTimeSeconds());
        System.out.println("Time in buildQuery - " + userInsertQuery.getTotalTimeMillis());

        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}