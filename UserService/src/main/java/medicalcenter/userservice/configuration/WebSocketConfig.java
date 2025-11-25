package medicalcenter.userservice.configuration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.configuration.security.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-support")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        registry.addEndpoint("/ws-private")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("🔗 WebSocket CONNECT attempt received");
                    log.info("   Session ID: {}", accessor.getSessionId());
                    log.info("   Origin: {}", accessor.getFirstNativeHeader("Origin"));
                    
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    log.info("   Authorization header present: {}", authHeader != null);
                    
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        log.info("   Token length: {}", token.length());
                        log.info("   Token preview: {}...", token.substring(0, Math.min(20, token.length())));
                        
                        try {
                            if (jwtUtil.isTokenValid(token)) {
                                String userPhone = jwtUtil.extractUsername(token);
                                List<String> roles = jwtUtil.extractRoles(token);
                                
                                log.info("✅ WebSocket AUTHENTICATED: {} with roles: {}", userPhone, roles);
                                
                                var authorities = roles.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList());

                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                        userPhone,
                                        null,
                                        authorities
                                );
                                
                                accessor.setUser(auth);
                                log.info("   Authentication successfully set for user: {}", userPhone);
                            } else {
                                log.warn("❌ WebSocket Token validation failed");
                                setAnonymousAuthentication(accessor, "Token validation failed");
                            }
                        } catch (Exception e) {
                            log.error("🚨 WebSocket Authentication error: {}", e.getMessage());
                            setAnonymousAuthentication(accessor, "Authentication error: " + e.getMessage());
                        }
                    } else {
                        if (authHeader == null) {
                            log.warn("❌ WebSocket NO Authorization header");
                        } else {
                            log.warn("❌ WebSocket Malformed Authorization header (no Bearer prefix)");
                            log.info("   Auth header: {}", authHeader);
                        }
                        setAnonymousAuthentication(accessor, "No valid Authorization header");
                    }
                }
                
                // Логирование для отладки других команд
                if (accessor != null && accessor.getUser() != null) {
                    log.debug("WebSocket {} by user: {} to: {}", 
                            accessor.getCommand(), 
                            accessor.getUser().getName(), 
                            accessor.getDestination());
                }
                
                return message;
            }
            
            private void setAnonymousAuthentication(StompHeaderAccessor accessor, String reason) {
                UsernamePasswordAuthenticationToken anonymousAuth = new UsernamePasswordAuthenticationToken(
                        "anonymous",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                );
                accessor.setUser(anonymousAuth);
                log.info("🔓 WebSocket set as ANONYMOUS - Reason: {}", reason);
            }
        });
    }
}