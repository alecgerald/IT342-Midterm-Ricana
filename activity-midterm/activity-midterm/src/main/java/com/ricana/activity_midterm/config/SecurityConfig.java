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
    public SecurityFilterChain defaultSecurityChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll() // Allow access to landing page and static resources
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/contacts", true) // Redirect to /contacts after successful login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Custom logout URL
                        .logoutSuccessUrl("/") // Redirect to login page after logout
                        .invalidateHttpSession(true) // Invalidate session
                        .deleteCookies("JSESSIONID") // Delete cookies
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF (ensure this is intentional)
                .build();
    }
}