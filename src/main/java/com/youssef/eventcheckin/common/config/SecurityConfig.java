package com.youssef.eventcheckin.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // door staff and organizers can scan
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/*/check-ins").hasAnyRole("ORGANIZER", "STAFF")

                        // organizers only
                        .requestMatchers(HttpMethod.POST, "/api/v1/events").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/*/publish").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/*/registrations").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/attendees").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/registrations/*").hasRole("ORGANIZER")

                        // everything else: logged in is enough
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
