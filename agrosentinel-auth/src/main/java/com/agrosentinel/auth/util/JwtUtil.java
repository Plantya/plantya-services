package com.agrosentinel.auth.util;

import io.smallrye.jwt.build.Jwt;

public class JwtUtil {

    public static String generateToken(String userId, String username, String role, long ttlMillis) {
        long now = System.currentTimeMillis();
        long expired = now + ttlMillis;

        return Jwt.claims()
                .issuer("agro-sentinel")
                .subject(userId)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now / 1000)
                .expiresAt(expired / 1000)
                .sign();
    }

}
