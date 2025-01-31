package com.satish.authandauthorization.oauth2_jan2025.handlers;

import com.satish.authandauthorization.oauth2_jan2025.Services.JwtService;
import com.satish.authandauthorization.oauth2_jan2025.Services.UserService;
import com.satish.authandauthorization.oauth2_jan2025.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;


@Component
public class OAuth2SuccessCallbackHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String photoUrl = oAuth2User.getAttribute("picture");

        // creating the User Object

//        User user = new User();
//        user.setEmail(email);
//        user.setName(name);
//        user.setPhoto(photoUrl);
//
//        if(userService.findByEmail(email).isPresent()){
//            // checking if the user is already present in the database
//            System.out.println(name +" is trying to log in");
//
//        }else{
//            // user is not present and we need to save the user in the database
//            System.out.println(name+" is trying to sign up");
//            userService.save(user);
//        }



        User user = userService.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPhoto(photoUrl);
            userService.save(newUser);
            return newUser;
        });


        //Similarly generate one refresh token that would be used to generate multiple jwt_tokens
        // these jwt_tokens will be short-lived but the refresh token will be active for a larger time period
        // this refresh token will be used to generate jwt_token after they get expired
        // Both Refresh token and the jwt_token will be stored in the databse.
        // Once the refresh token also gets expired the user will be forced to login again and
        // a new refresh and jwt_token will be generated.

        // Generate Refresh Token
        String refreshToken = UUID.randomUUID().toString(); // Use a UUID for simplicity
        Date refreshTokenExpiry = new Date(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)); // 30 days

        String token = jwtService.generateToken(user);

        // Store tokens in the database
        user.setJwtToken(token);
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(refreshTokenExpiry);
        userService.save(user);


        // Send tokens as cookies
        Cookie jwtCookie = new Cookie("jwt_token", token);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);

        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);

        response.addCookie(jwtCookie);
        response.addCookie(refreshCookie);

        getRedirectStrategy().sendRedirect(request, response, "/secured");
        super.onAuthenticationSuccess(request, response, authentication);
    }




}
