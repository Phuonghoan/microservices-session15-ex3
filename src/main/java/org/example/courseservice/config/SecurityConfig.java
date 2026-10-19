package org.example.courseservice.config;

import org.example.courseservice.security.GatewayRoleFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // Tắt CSRF
                .csrf(csrf -> csrf.disable())

                // Không sử dụng Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Chưa xác thực trả 401
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                )

                // API Course yêu cầu có Authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/courses",
                                "/api/courses/**"
                        ).authenticated()

                        .anyRequest().denyAll()
                )

                // Đọc Role từ Header trước khi phân quyền
                .addFilterBefore(
                        new GatewayRoleFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
