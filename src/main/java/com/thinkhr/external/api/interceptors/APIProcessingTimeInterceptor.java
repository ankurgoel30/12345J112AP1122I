package com.thinkhr.external.api.interceptors;


import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.thinkhr.external.api.services.utils.CommonUtil;

/**
 * Interceptor to log each API request's start time, end time
 * and total time taken to process request
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-14
 *
 */
public class APIProcessingTimeInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory
            .getLogger(APIProcessingTimeInterceptor.class);

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest,
     *  javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        long startTime = System.currentTimeMillis();

        String jobId = UUID.randomUUID().toString();

        logger.info("Request JobId/RequestReferenceId: " + jobId);
        logger.info(" Request URL::" + request.getRequestURL().toString());
        logger.info("API execution Start Time " + CommonUtil.getTodayInUTC());

        request.setAttribute("startTime", startTime);
        request.setAttribute("jobId", jobId);

        //if returned false, we need to make sure 'response' is sent
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest,
     *  javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        //TODO - Keep it for more enhancements 
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#afterCompletion(javax.servlet.http.HttpServletRequest,
     *  javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex)
                    throws Exception {
        logger.info("API execution End Time " + CommonUtil.getTodayInUTC());
    }

}