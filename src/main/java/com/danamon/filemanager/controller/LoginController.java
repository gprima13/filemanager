package com.danamon.filemanager.controller;

import com.danamon.filemanager.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    password
                            )
                    );

            String token = jwtService.generateToken(authentication.getName());

            Cookie cookie = new Cookie(
                            "ACCESS_TOKEN",
                            token
                    );
            cookie.setHttpOnly(true);
            // Local HTTP development
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);
            return "redirect:/";

        } catch (AuthenticationException e) {
            return "redirect:/login?error";
        }
    }

    @PostMapping("/logout")
    public String logout(
            HttpServletResponse response) {
        Cookie cookie =
                new Cookie(
                        "ACCESS_TOKEN",
                        ""
                );
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login?logout";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}
