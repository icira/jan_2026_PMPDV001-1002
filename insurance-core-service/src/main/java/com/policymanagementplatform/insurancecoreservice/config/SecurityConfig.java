package com.policymanagementplatform.insurancecoreservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // For API testing tools like Bruno/Postman, CSRF is annoying and not needed for stateless APIs.
                .csrf(csrf -> csrf.disable())

                // Allow all API endpoints without login for Sprint 1.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                )

                // Disable default form login page that you're seeing.
                .formLogin(form -> form.disable())

                // Also disable basic auth popups.
                .httpBasic(basic -> basic.disable())

                .build();
    }
}

