package com.thinkhr.external.api.interceptors;

import static com.thinkhr.external.api.ApplicationConstants.AUTHORIZATION_HEADER;
import static com.thinkhr.external.api.ApplicationConstants.BEARER_TOKEN;
import static com.thinkhr.external.api.request.APIRequestHelper.*;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.helpers.JwtHelper;
import com.thinkhr.external.api.model.AppAuthData;
import com.thinkhr.external.api.request.APIRequestHelper;
import com.thinkhr.external.api.services.AuthorizationManager;

/**
 * @author Surabhi Bhawsar
 * @Since 2017-12-11
 *
 */
public class JwtTokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired 
    AuthorizationManager authorizationManager; 
    
    @Value("${JWT.jwt_key}")
    private String key;

    @Value("${JWT.jwt_iss}")
    private String iss;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith(BEARER_TOKEN)) {
            throw ApplicationException.createAuthorizationError(APIErrorCodes.AUTHORIZATION_MISSING);
        }

        final String token = authHeader.substring(BEARER_TOKEN.length());

        try {
            AppAuthData appAuthData = JwtHelper.decodeAndPrepareModel(token, authHeader, token);
            //Setting data to request attributes which we can fetch in future
            request.setAttribute("appAuthData", appAuthData);
            return authorizationManager.checkAuthorization(appAuthData);
        } catch (UnsupportedEncodingException e) {
            ApplicationException.createAuthorizationError(APIErrorCodes.AUTHORIZATION_FAILED, e.getMessage());
        } catch (JWTVerificationException e) {
            ApplicationException.createAuthorizationError(APIErrorCodes.AUTHORIZATION_FAILED, e.getMessage());
        }

        return false;
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

}
