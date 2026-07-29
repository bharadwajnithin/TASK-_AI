package com.taskflowai.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

public class CookieUtils {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean isSecure) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .maxAge(maxAge);

        if (isSecure) {
            builder.secure(true).sameSite("None");
        } else {
            builder.sameSite("Lax");
        }

        response.addHeader("Set-Cookie", builder.build().toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    ResponseCookie cookieHeader = ResponseCookie.from(name, "")
                            .path("/")
                            .httpOnly(true)
                            .maxAge(0)
                            .sameSite("Lax")
                            .build();
                    response.addHeader("Set-Cookie", cookieHeader.toString());
                }
            }
        }
    }
}
