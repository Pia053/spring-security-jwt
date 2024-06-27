package com.example.demo.configuration;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class AppConfig {

    private final UserService userService;
    private final PreFilter preFilter;

    // Chỉ cho phép acceptoken
    private String[] WHITE_LIST = {"/auth/**"};

//    Thiết lập CORS domain sử dụng backend
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("**")
                        .allowedOrigins("http://localhost:8500")
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
                        .allowedHeaders("*") // Allowed request headers
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

//    1: PasswordEndCoder : Mã hóa mật khẩu
    @Bean
    public PasswordEncoder gestPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    2: SecurityFilterChain
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)  // chặn truy cập
                .authorizeHttpRequests(authorizeRequest ->
                        // Cho phép truy cập mà không cần token. mọi request khác phải authen
                        authorizeRequest.requestMatchers(WHITE_LIST).permitAll().anyRequest().authenticated()
                ).sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Lưu token phía server
                .authenticationProvider(provider()).addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class); // trỏ đến database

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity ->
                webSecurity.ignoring()
                        .requestMatchers("/actuator/**", "/v3/**", "/webjars/**", "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**");
    }

    // 2.1 Tạo provider như UserDfetailService: chứa user and PreFilter.class
    @Bean
    public AuthenticationProvider provider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService.userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(gestPasswordEncoder());

        return daoAuthenticationProvider;
    }

//    3. Authentication quản lý role, user truy cập vào hệ thống
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
