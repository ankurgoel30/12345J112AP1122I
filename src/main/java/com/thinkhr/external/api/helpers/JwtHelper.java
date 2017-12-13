package com.thinkhr.external.api.helpers;

import static com.thinkhr.external.api.ApplicationConstants.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.thinkhr.external.api.model.AppAuthData;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 *
 * Helper to decode a JWT token
 *
 * @author Sudhakar Kaki
 * @Since 2017-11-28
 *
 *
 */

public class JwtHelper {

    public static DecodedJWT decode(String key, String iss, String token) throws UnsupportedEncodingException, JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(iss)
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
        return jwt;
    }
    
    /**
     * To decode and prepare a Token object based on information available on JWT token
     * 
     * @param key
     * @param iss
     * @param token
     * @return
     * @throws UnsupportedEncodingException
     * @throws JWTVerificationException
     */
    public static AppAuthData decodeAndPrepareModel(String key, String iss, String token) 
            throws UnsupportedEncodingException, JWTVerificationException {
        
        DecodedJWT jwt = decode(key, iss, token);
        
        if (jwt != null) {
            Map<String, Claim> claims = jwt.getClaims();
            if (claims != null) {
                return prepareAuthToken(claims);
            }
        }
        
        return null;
    }

    /**
     * Get the values from Claims and set it to AppAuthToken
     * 
     * @param claims
     * @return
     */
    public static AppAuthData prepareAuthToken(Map<String, Claim> claims) {
        Claim clientId = claims.get(JWT_TOKEN_THR_CLIENT_ID);
        Claim sub = claims.get(JWT_TOKEN_THR_SUB);
        Claim brokerId = claims.get(JWT_TOKEN_THR_BROKER_ID);
        Claim user = claims.get(JWT_TOKEN_THR_USER);
        Claim issClaim = claims.get(JWT_TOKEN_THR_ISS);
        Claim role = claims.get(JWT_TOKEN_THR_ROLE);

        AppAuthData authToken = new AppAuthData();
        if (brokerId != null) {
            authToken.setBrokerId(brokerId.asInt());
        }
        if (clientId != null) {
            authToken.setClientId(clientId.asInt());
        }
        if (user != null) {
            authToken.setUser(user.asString());
        }
        if (issClaim != null) {
            authToken.setIss(issClaim.asString());
        }
        if (role != null) {
            authToken.setRole(role.asString());
        }
        if (sub != null) {
            authToken.setSub(sub.asString());
        }
        return authToken;
    }
}
