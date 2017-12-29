package com.thinkhr.external.api.interceptors;

import static com.thinkhr.external.api.ApplicationConstants.AUTHORIZATION_HEADER;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_BROKER_ID;
import static com.thinkhr.external.api.ApplicationConstants.DEVELOPMENT_ENV;
import static com.thinkhr.external.api.ApplicationConstants.BEARER_TOKEN;
import static com.thinkhr.external.api.ApplicationConstants.APP_AUTH_DATA;
import static com.thinkhr.external.api.ApplicationConstants.BROKER_ID_PARAM;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.helpers.JwtHelper;
import com.thinkhr.external.api.model.AppAuthData;
import com.thinkhr.external.api.services.AuthorizationManager;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Surabhi Bhawsar
 * @Since 2017-12-11
 *
 */
@Data   
@AllArgsConstructor
public class JwtTokenInterceptor extends HandlerInterceptorAdapter {

    private String key;

    private String iss;

    private AuthorizationManager authorizationManager;
    
    private String environment;;
    
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        
        if (DEVELOPMENT_ENV.equalsIgnoreCase(environment.trim())) {
            setDefaults(request); 
            return true;
        }

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith(BEARER_TOKEN)) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.AUTHORIZATION_MISSING);
        }

        final String token = authHeader.substring(BEARER_TOKEN.length());

        try {
            AppAuthData appAuthData = JwtHelper.decodeAndPrepareModel(key, iss, token);
            //Setting data to request attributes which we can fetch in future
            request.setAttribute(APP_AUTH_DATA, appAuthData);
            request.setAttribute(BROKER_ID_PARAM, appAuthData.getBrokerId());
            return authorizationManager.checkAuthorization(appAuthData);
        } catch (UnsupportedEncodingException e) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.AUTHORIZATION_FAILED, e.getMessage());
        } catch (JWTVerificationException e) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.AUTHORIZATION_FAILED, e.getMessage());
        }

    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        //TODO - Keep it for more enhancements 
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex)
                    throws Exception {
        //DO Nothing
    }
    
    /**
     * Setting default attribute in request parameter.
     * 
     * @param request
     */
    private void setDefaults(HttpServletRequest request) {
        request.setAttribute(BROKER_ID_PARAM, DEFAULT_BROKER_ID);
    }

}
