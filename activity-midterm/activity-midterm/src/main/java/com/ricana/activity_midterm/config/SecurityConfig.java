package com.ricana.activity_midterm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll() // Public access
                        .anyRequest().authenticated() // Secure all other endpoints
                )
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/contacts", true) // Redirect after login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Custom logout URL
                        .logoutSuccessUrl("/") // Redirect to home after logout
                        .invalidateHttpSession(true) // Clear session
                        .deleteCookies("JSESSIONID") // Remove cookies
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF (for simplicity)
                .build();
    }
}