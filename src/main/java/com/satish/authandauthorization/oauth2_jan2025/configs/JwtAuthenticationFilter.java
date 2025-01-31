package com.satish.authandauthorization.oauth2_jan2025.configs;

import com.satish.authandauthorization.oauth2_jan2025.Services.JwtService;
import com.satish.authandauthorization.oauth2_jan2025.Services.UserService;
import com.satish.authandauthorization.oauth2_jan2025.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try{
            String jwt = getJwtFromRequest(request);

            if(StringUtils.hasText(jwt)){
                String userEmail = jwtService.validateToken(jwt);
//                User user = userService.findByEmail(userEmail).
//                        orElseThrow(()->new RuntimeException("User not found"));


            }
        } catch (Exception e) {
          logger.error("Error while validating token", e);
         // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.sendRedirect("/login");
        }
     filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("jwt_token")){
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
