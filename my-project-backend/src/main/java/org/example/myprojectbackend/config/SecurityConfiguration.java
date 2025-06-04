package org.example.myprojectbackend.config;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.myprojectbackend.entity.RestBean;
import org.example.myprojectbackend.entity.vo.response.AuthorizeVO;
import org.example.myprojectbackend.filter.JwtAuthorizeFilter;
import org.example.myprojectbackend.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    @Resource
    JwtUtils jwtUtils;

    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(conf->conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::unAuthorized)
                )
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::unAuthorized)
                        .accessDeniedHandler(this::unAccessDenied)
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    public void unAuthorized(HttpServletRequest req,
                             HttpServletResponse resp,
                             AuthenticationException exception) throws IOException, ServletException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().write(RestBean.failure(exception.getMessage()).asJsonString());
    }

    public void unAccessDenied(HttpServletRequest req,
                               HttpServletResponse resp,
                               AccessDeniedException exception) throws IOException, ServletException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().write(RestBean.forbidden(exception.getMessage()).asJsonString());
    }

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        String token = jwtUtils.createJwt(user, 1, "Tim");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        AuthorizeVO authorizeVO = new AuthorizeVO();
        authorizeVO.setExpiresAt(jwtUtils.expireTime());
        authorizeVO.setToken(token);
        authorizeVO.setRole("");
        authorizeVO.setUsername("");
        response.getWriter().println(RestBean.success(authorizeVO).asJsonString());
    }
/*    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(RestBean.failure(exception.getMessage()).asJsonString());
    }*/
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String authorization = request.getHeader("Authorization");
        if (jwtUtils.invalidateJWT(authorization)) {
            writer.write(RestBean.success().asJsonString());
        }else{
            writer.write(RestBean.failure("退出登录失败").asJsonString());
        }
    }

}
