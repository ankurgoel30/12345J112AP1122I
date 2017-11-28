package com.thinkhr.external.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.thinkhr.external.api.interceptors.APIProcessingTimeInterceptor;

/**
 * Application configuration class
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-14
 *
 */
@Configuration
@EnableWebMvc
public class AppConfig extends WebMvcConfigurerAdapter {
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new APIProcessingTimeInterceptor()).addPathPatterns("/v1/**");
    }
}