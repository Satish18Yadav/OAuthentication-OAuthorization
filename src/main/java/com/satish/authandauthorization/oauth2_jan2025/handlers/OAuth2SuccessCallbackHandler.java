package com.satish.authandauthorization.oauth2_jan2025.handlers;

import com.satish.authandauthorization.oauth2_jan2025.Services.UserService;
import com.satish.authandauthorization.oauth2_jan2025.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class OAuth2SuccessCallbackHandler extends SimpleUrlAuthenticationSuccessHandler {

    private UserService userService;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String photoUrl = oAuth2User.getAttribute("picture");

        // creating the User Object

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPhoto(photoUrl);

        if(userService.findByEmail(email).isPresent()){
            // checking if the user is already present in the database
            System.out.println(name +" is trying to log in");

        }else{
            // user is not present and we need to save the user in the database
            userService.save(user);
        }
        super.onAuthenticationSuccess(request, response, authentication);

    }

}
