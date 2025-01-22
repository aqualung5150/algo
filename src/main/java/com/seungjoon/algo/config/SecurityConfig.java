package com.seungjoon.algo.config;

import com.seungjoon.algo.auth.jwt.JwtExceptionFilter;
import com.seungjoon.algo.auth.jwt.JwtFilter;
import com.seungjoon.algo.auth.oauth.OAuth2UserService;
import com.seungjoon.algo.auth.oauth.OAuth2SuccessHandler;
import com.seungjoon.algo.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    //Authentication
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    //Filter
    private final JwtFilter jwtFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    //RequestMatcher
    private final RequestMatcherManager requestMatcherManager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults()) //TEST
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtFilter.class)

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(requestMatcherManager.getRequestMatchersByRole(Role.USERNAME_UNSET)).hasAuthority(Role.USERNAME_UNSET.name())
                        .requestMatchers(requestMatcherManager.getRequestMatchersByRole(Role.MEMBER)).hasAnyAuthority(Role.MEMBER.name(), Role.ADMIN.name())
                        .requestMatchers(requestMatcherManager.getRequestMatchersByRole(null)).permitAll()
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();
    }
}
