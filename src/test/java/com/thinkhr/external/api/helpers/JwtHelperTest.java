package com.thinkhr.external.api.helpers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.thinkhr.external.api.ApiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 * Testing the Helper to decode a JWT token
 *
 * @author Sudhakar Kaki
 * @Since 2017-11-28
 *
 *
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class JwtHelperTest {

    @Value("${JWT.jwt_key}")
    private String key;

    @Value("${JWT.jwt_iss}")
    private String iss;

    private String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ0aGlua2hyLmNvbSIsImV4cCI6MTA5NzI3MjI3NDksInN1YiI6MzMwMTgwLCJ0aHI6YnJva2VySWQiOjgxNDgsInRocjpjbGllbnRJZCI6ODE0OCwidGhyOnVzZXIiOiJza2FraUB0aGlua2hyLmNvbSIsInRocjpyb2xlIjoiQWRtaW5pc3RyYXRvciJ9.ApZmiWgpfJzqKA-NVPu-DgKTwPLKcScJIza6ojiFXek";

    @Test
    public void testDecode() throws Exception {

        try {
            DecodedJWT jwt = JwtHelper.decode(key, iss, token);
            Map<String, Claim> claims = jwt.getClaims();

            Claim clientId = claims.get("thr:clientId");
            assertEquals(clientId.asInt(), (Integer) 8148);

            Claim sub = claims.get("sub");
            assertEquals(sub.asInt(), (Integer) 330180);

            Claim brokerId = claims.get("thr:brokerId");
            assertEquals(brokerId.asInt(), (Integer) 8148);

            Claim user = claims.get("thr:user");
            assertEquals(user.asString(), "skaki@thinkhr.com");

            Claim iss = claims.get("iss");
            assertEquals(iss.asString(), "thinkhr.com");

            Claim role = claims.get("thr:role");
            assertEquals(role.asString(), "Administrator");
        } catch (UnsupportedEncodingException e) {
            fail("Failed to Decode JWT");
        } catch (JWTVerificationException e) {
            fail("Failed to Verify JWT");
        }
    }

    @Test
    public void testDecodeNegative() throws Exception {

        try {
            DecodedJWT jwt = JwtHelper.decode(key, iss, token + " Fail");
            fail("Failed a negative test in JWT");
        } catch (UnsupportedEncodingException e) {
            assertTrue(true);
        } catch (JWTVerificationException e) {
            assertTrue(true);
        }
    }
}