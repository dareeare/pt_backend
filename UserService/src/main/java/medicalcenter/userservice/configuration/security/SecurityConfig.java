package medicalcenter.userservice.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        
                        // WebSocket endpoints - handshake allowed, auth in interceptor
                        .requestMatchers("/ws-support/**", "/ws-private/**", "/ws/**").permitAll()
                        
                        // File uploads access
                        .requestMatchers("/uploads/**", "/avatars/**").permitAll()
                        
                        // Health check and monitoring
                        .requestMatchers("/actuator/health", "/health").permitAll()
                        
                        // Support chat API endpoints - require operator/admin role
                        .requestMatchers("/api/support/**").hasAnyRole("OPERATOR", "ADMIN")
                        
                        // User management endpoints
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/doctors/**").hasAnyRole("OPERATOR", "ADMIN", "DOCTOR")
                        .requestMatchers("/api/patients/**").hasAnyRole("OPERATOR", "ADMIN", "DOCTOR")
                        .requestMatchers("/api/operators/**").hasAnyRole("ADMIN")
                        
                        // WebSocket message destinations - require authentication
                        .requestMatchers("/app/private/**").authenticated()
                        .requestMatchers("/app/support/**").hasAnyRole("OPERATOR", "ADMIN")
                        
                        // Private chat API endpoints - require authentication
                        .requestMatchers("/api/private/chat/**").authenticated()
                        
                        // All other API endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}