package com.satish.authandauthorization.oauth2_jan2025.configs;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/").permitAll();
            auth.anyRequest().authenticated();
        }).oauth2Client(Customizer.withDefaults()).oauth2Login(Customizer.withDefaults()).logout(logout -> logout
                .logoutUrl("/logout")  // URL to trigger logout
                .logoutSuccessUrl("/")  // Redirect URL after successful logout
                .invalidateHttpSession(true)  // Invalidate session
                .deleteCookies("JSESSIONID")  // Delete cookies for session management
        ).build();
       
    }
}
