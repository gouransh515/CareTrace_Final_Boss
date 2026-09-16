package com.krishu.caretracev2.Configuration;

import com.krishu.caretracev2.ClientRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtfilter;
    @Value("${allow.origin}")
    private String allowedOrigins;

    public SecurityConfig(JwtFilter jwtfilter) {
        this.jwtfilter = jwtfilter;
    }

    @Bean
    public BCryptPasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain getFilter(HttpSecurity https){
        https.csrf(AbstractHttpConfigurer::disable);
        https.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        https.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        https.authorizeHttpRequests(request->request.requestMatchers("/auth/signup","/auth/signin")
                .permitAll().anyRequest().authenticated());
        https.addFilterBefore(jwtfilter, UsernamePasswordAuthenticationFilter.class);
        return https.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173",allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
