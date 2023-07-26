package com.velikanovdev.antifraud.configuration;

import com.velikanovdev.antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final UserService userService;

    @Autowired
    public SecurityConfiguration(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/user/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/api/auth/list/**").hasAnyRole("SUPPORT", "ADMINISTRATOR")
                .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction/**").hasRole("MERCHANT")
                .requestMatchers("/api/antifraud/**").hasRole("SUPPORT")
                .requestMatchers(HttpMethod.PUT, "/api/auth/access/**").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT, "/api/auth/role/**").hasRole("ADMINISTRATOR")
                .requestMatchers("/actuator/shutdown").permitAll()
                .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable) // disabling CSRF will allow sending POST request using Postman
                .httpBasic(Customizer.withDefaults())
                .authenticationManager(authManager);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
