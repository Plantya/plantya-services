package com.agrosentinel.auth.service;

import com.agrosentinel.auth.model.entity.User;
import com.agrosentinel.auth.util.JwtUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.NewCookie;

@ApplicationScoped
public class CookieService {

    public NewCookie createJwtCookie(User user) {
        String token = JwtUtil.generateToken(
                user.getId().toString(),
                user.getUsername(),
                user.getRole().toString(),
                8 * 60 * 60 * 1000
        );

        return new NewCookie.Builder("access_token")
                .value(token)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .maxAge(8 * 60 * 60)
                .build();
    }

    public NewCookie deleteCookie() {
        return new NewCookie.Builder("access_token")
                .value("")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .maxAge(0)
                .build();
    }

}
