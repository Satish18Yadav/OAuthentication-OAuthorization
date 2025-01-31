package com.satish.authandauthorization.oauth2_jan2025.configs;


import com.satish.authandauthorization.oauth2_jan2025.handlers.OAuth2SuccessCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private OAuth2SuccessCallbackHandler successCallbackHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {

        return security.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/").permitAll();
            auth.anyRequest().authenticated();
        }).oauth2Client(Customizer.withDefaults()).oauth2Login(oauth -> {
            oauth.successHandler(successCallbackHandler);
        }).logout(logout -> logout
                .logoutUrl("/logout")  // URL to trigger logout
                .logoutSuccessUrl("/")  // Redirect URL after successful logout
                .invalidateHttpSession(true)  // Invalidate session
                .deleteCookies("JSESSIONID")  // Delete cookies for session management
        ).sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Ensuring stateless authentication
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Adding JWT filter before Spring's default authentication
                .build();
       
    }
}
