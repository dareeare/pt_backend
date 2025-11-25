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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
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
            
            if (accessor != null) {
                // КРИТИЧЕСКИЙ ФИКС: Устанавливаем SecurityContext для ВСЕХ команд
                if (accessor.getUser() != null && accessor.getUser() instanceof UsernamePasswordAuthenticationToken) {
                    UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) accessor.getUser();
                    
                    // Устанавливаем SecurityContext с правильной стратегией
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(auth);
                    SecurityContextHolder.setContext(context);
                    
                    log.debug("🔐 SecurityContext установлен для команды {} пользователя {}", 
                            accessor.getCommand(), auth.getName());
                } else if (accessor.getUser() == null) {
                    // Для анонимных пользователей тоже устанавливаем контекст
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(createAnonymousAuthentication());
                    SecurityContextHolder.setContext(context);
                }
                
                // Обработка CONNECT команды
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    handleConnect(accessor);
                }
            }
            
            return message;
        }
        
        private void handleConnect(StompHeaderAccessor accessor) {
            log.info("🔗 WebSocket CONNECT attempt received");
            
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                try {
                    if (jwtUtil.isTokenValid(token)) {
                        String userPhone = jwtUtil.extractUsername(token);
                        List<String> roles = jwtUtil.extractRoles(token);
                        
                        log.info("✅ WebSocket AUTHENTICATED: {} with roles: {}", userPhone, roles);
                        
                        var authorities = roles.stream()
                                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                userPhone,
                                null,
                                authorities
                        );
                        
                        // Устанавливаем аутентификацию
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(auth);
                        SecurityContextHolder.setContext(context);
                        accessor.setUser(auth);
                        
                        log.info("   Authentication successfully set for user: {}", userPhone);
                    } else {
                        log.warn("❌ WebSocket Token validation failed");
                        setAnonymousAuthentication(accessor);
                    }
                } catch (Exception e) {
                    log.error("🚨 WebSocket Authentication error: {}", e.getMessage());
                    setAnonymousAuthentication(accessor);
                }
            } else {
                log.warn("❌ WebSocket NO valid Authorization header");
                setAnonymousAuthentication(accessor);
            }
        }
        
        private void setAnonymousAuthentication(StompHeaderAccessor accessor) {
    UsernamePasswordAuthenticationToken anonymousAuth = new UsernamePasswordAuthenticationToken(
            "anonymous",
            null,
            List.of(new SimpleGrantedAuthority("ROLE_USER")) // Теперь ROLE_USER вместо ROLE_ANONYMOUS
    );
    SecurityContextHolder.getContext().setAuthentication(anonymousAuth);
    accessor.setUser(anonymousAuth);
    log.info("🔓 WebSocket set as ANONYMOUS with USER role - Reason: {}");
}
        
        private UsernamePasswordAuthenticationToken createAnonymousAuthentication() {
            return new UsernamePasswordAuthenticationToken(
                    "anonymous",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
            );
        }
    });
}
}