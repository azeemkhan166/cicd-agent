package com.realcoderz.util;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilTest {

    private JWTUtil jwtUtil;
    private UserDetails testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil();
        testUser = User.withUsername("101")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtUtil.generateToken(testUser);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_isThreePartJWT() {
        String token = jwtUtil.generateToken(testUser);
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT must have header.payload.signature");
    }

    @Test
    void extractSubject_returnsCorrectUsername() {
        String token = jwtUtil.generateToken(testUser);
        String subject = jwtUtil.extractSubject(token);
        assertEquals("101", subject);
    }

    @Test
    void validateToken_withMatchingUserDetails_returnsTrue() {
        String token = jwtUtil.generateToken(testUser);
        assertTrue(jwtUtil.validateToken(token, testUser));
    }

    @Test
    void validateToken_withDifferentUser_returnsFalse() {
        String token = jwtUtil.generateToken(testUser);
        UserDetails anotherUser = User.withUsername("999")
                .password("pass")
                .authorities(Collections.emptyList())
                .build();
        assertFalse(jwtUtil.validateToken(token, anotherUser));
    }

    @Test
    void validateToken_singleArg_returnsTrueForValidToken() {
        String token = jwtUtil.generateToken(testUser);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_singleArg_returnsFalseForTamperedToken() {
        String token = jwtUtil.generateToken(testUser);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    void validateToken_singleArg_returnsFalseForGarbage() {
        assertFalse(jwtUtil.validateToken("not.a.jwt"));
    }

    @Test
    void isTokenExpired_freshToken_returnsFalse() {
        String token = jwtUtil.generateToken(testUser);
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void extractExpiration_isTenHoursFromNow() {
        long before = System.currentTimeMillis();
        String token = jwtUtil.generateToken(testUser);
        long expiry = jwtUtil.extractExpiration(token).getTime();
        long after = System.currentTimeMillis();

        long tenHoursMs = 10L * 60 * 60 * 1000;
        assertTrue(expiry >= before + tenHoursMs - 1000);
        assertTrue(expiry <= after + tenHoursMs + 1000);
    }
}
