package com.thinkhr.external.api.helpers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;

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
}
