package com.example.projectmanager.config;


import com.example.projectmanager.util.JwtUtil;
import com.example.projectmanager.config.JwtConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.*;

@EnableMethodSecurity(prePostEnabled = true)
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return mock(JwtUtil.class);
    }

    @Bean
    public JwtConfig jwtConfig() {
        JwtConfig config = mock(JwtConfig.class);
        when(config.getSecretKey()).thenReturn(
                new javax.crypto.spec.SecretKeySpec("секретныйключсекретныйключ123456".getBytes(), "HmacSHA256")
        );
        return config;
    }

    @Bean
    public com.example.projectmanager.service.CustomUserDetailsService customUserDetailsService() {
        return mock(com.example.projectmanager.service.CustomUserDetailsService.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}