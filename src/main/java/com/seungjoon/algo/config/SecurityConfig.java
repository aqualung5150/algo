package com.seungjoon.algo.config;

import com.seungjoon.algo.auth.AuthenticationFailureHandler;
import com.seungjoon.algo.auth.AuthenticationSuccessHandler;
import com.seungjoon.algo.auth.JsonAuthenticationFilter;
import com.seungjoon.algo.auth.jwt.JwtExceptionFilter;
import com.seungjoon.algo.auth.jwt.JwtFilter;
import com.seungjoon.algo.auth.jwt.JwtProvider;
import com.seungjoon.algo.auth.oauth.OAuth2FailureHandler;
import com.seungjoon.algo.auth.oauth.OAuth2SuccessHandler;
import com.seungjoon.algo.auth.oauth.OAuth2UserService;
import com.seungjoon.algo.auth.service.CustomLogoutSuccessHandler;
import com.seungjoon.algo.member.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final UserDetailsService userDetailsService;
    private final OAuth2UserService oAuth2UserService;
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter(), JwtFilter.class)
                .addFilterAt(jsonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler())
                )

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler())
                        .failureHandler(oAuth2FailureHandler())
                )

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(requestMatcherManager().getRequestMatchersByRole(Role.USERNAME_UNSET)).hasAuthority(Role.USERNAME_UNSET.name())
                        .requestMatchers(requestMatcherManager().getRequestMatchersByRole(Role.MEMBER)).hasAnyAuthority(Role.MEMBER.name(), Role.ADMIN.name())
                        .requestMatchers(requestMatcherManager().getRequestMatchersByRole(null)).permitAll()
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

    @Bean
    public RequestMatcherManager requestMatcherManager() {
        return new RequestMatcherManager();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtProvider, requestMatcherManager());
    }

    @Bean
    public JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwtProvider);
    }

    @Bean
    public OAuth2FailureHandler oAuth2FailureHandler() { return new OAuth2FailureHandler(); }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler(jwtProvider);
    }

    @Bean public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler();
    }

    @Bean
    public JsonAuthenticationFilter jsonAuthenticationFilter() throws Exception {

        JsonAuthenticationFilter jsonAuthenticationFilter = new JsonAuthenticationFilter("/login");
        jsonAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jsonAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        jsonAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return jsonAuthenticationFilter;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }
}
