package org.example.courseservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class GatewayRoleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Đọc Role từ Header do Gateway truyền xuống
        String role = request.getHeader("X-User-Role");

        if (role != null && !role.isBlank()) {

            // 2. Chuyển Role thành Authority
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(role.trim());

            // 3. Tạo Authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            "gateway-user",
                            null,
                            List.of(authority)
                    );

            // 4. Đưa Authentication vào SecurityContext
            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            System.out.println(
                    "Course-Service nhận X-User-Role: " + role
            );
        }

        // 5. Cho request tiếp tục
        filterChain.doFilter(request, response);
    }
}
