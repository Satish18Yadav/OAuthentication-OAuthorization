package com.satish.authandauthorization.oauth2_jan2025.Controller;

import com.satish.authandauthorization.oauth2_jan2025.Services.JwtService;
import com.satish.authandauthorization.oauth2_jan2025.Services.UserService;
import com.satish.authandauthorization.oauth2_jan2025.models.User;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;


//    @PostMapping("/refresh-token")
//    public ResponseEntity<?> refreshJwtToken(@CookieValue("refresh_token") String refreshToken) {
//        User user = userService.findByRefreshToken(refreshToken)
//                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
//
//        if (user.getRefreshTokenExpiry().before(new Date())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
//        }
//
//        String newJwtToken = jwtService.generateToken(user);
//        user.setJwtToken(newJwtToken);
//        userService.save(user);
//
//        Cookie jwtCookie = new Cookie("jwt_token", newJwtToken);
//        jwtCookie.setPath("/");
//        jwtCookie.setHttpOnly(true);
//
//        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).build();
//    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshJwtToken(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        // If the refresh token is missing
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        // Find user by refresh token
        Optional<User> optionalUser = userService.findByRefreshToken(refreshToken);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        User user = optionalUser.get();

        // Check if refresh token is expired
        if (user.getRefreshTokenExpiry().before(new Date())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        }

        // Generate a new JWT token
        String newJwtToken = jwtService.generateToken(user);

        // Generate a new refresh token
        String newRefreshToken = jwtService.generateRefreshToken(user);
        Date newRefreshExpiry = jwtService.getRefreshTokenExpiry(); // Get new expiry date

        // Update user details
        user.setJwtToken(newJwtToken);
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(newRefreshExpiry);
        userService.save(user);

        // Create JWT cookie
        Cookie jwtCookie = new Cookie("jwt_token", newJwtToken);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);

        // Create new Refresh Token cookie
        Cookie refreshCookie = new Cookie("refresh_token", newRefreshToken);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);

        // Return response with updated tokens
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }
}