package com.thinkhr.external.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.thinkhr.external.api.interceptors.APIProcessingTimeInterceptor;
import com.thinkhr.external.api.interceptors.JwtTokenInterceptor;
import com.thinkhr.external.api.services.AuthorizationManager;

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
    
    @Value("${app.environment}")
    private String environment;

    @Autowired 
    AuthorizationManager authorizationManager; 
    
    @Value("${JWT.jwt_key}")
    private String key;

    @Value("${JWT.jwt_iss}")
    private String iss;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new APIProcessingTimeInterceptor()).addPathPatterns("/v1/**");
        registry.addInterceptor(new JwtTokenInterceptor(key, iss, authorizationManager, environment))
                .addPathPatterns("/v1/**");
    }
}