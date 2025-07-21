package com.example.StageDIP.security;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    // Your JWT Filter Bean
    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter(jwtUtils, userDetailsService);
    }

    // Password Encoder Bean (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // DAO Authentication Provider to tell Spring how to load user and check passwords
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // AuthenticationManager Bean wired with the DaoAuthenticationProvider
    @Bean
    public AuthenticationManager authenticationManager() {
        return new org.springframework.security.authentication.ProviderManager(authenticationProvider());
    }

    // Main security filter chain: disable CSRF, stateless session, JWT filter, endpoints
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
    	  .cors()  // <-- Add this line to enable Spring Security to use your CorsConfig
    	  .and()
    	  .csrf(csrf -> csrf.disable())
    	  .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    	  .authorizeHttpRequests(auth -> auth
    	      .requestMatchers("/api/auth/**").permitAll()
    	      .requestMatchers("/api/consultations/**").hasAnyRole("ADMIN", "USER") // ← 👈 ADD THIS LINE
    	      .requestMatchers("/api/files/upload").hasAnyRole("ADMIN", "USER") // Add this

    	      .anyRequest().authenticated()
    	  )
    	  .authenticationProvider(authenticationProvider())
    	  .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
