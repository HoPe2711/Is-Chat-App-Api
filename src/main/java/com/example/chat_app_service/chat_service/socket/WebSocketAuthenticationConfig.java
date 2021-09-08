package com.example.chat_app_service.chat_service.socket;

import com.example.chat_app_service.authen_service.security.filter.JWT.JwtUtils;
import com.example.chat_app_service.authen_service.security.filter.service.UserDetailsServiceImplement;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Objects;

import static com.example.chat_app_service.authen_service.security.SecurityConstants.SECRET;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private UserDetailsServiceImplement userDetailsServiceImplement;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String jwtToken = accessor.getFirstNativeHeader("Authorization");
                    if (jwtToken != null && jwtUtils.validateJwtToken(jwtToken)) {
                        System.out.println(2);
                        String username = jwtUtils.getUserNameFromJwtToken(jwtToken,SECRET);
                        UserDetails userDetails = userDetailsServiceImplement.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, null);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        accessor.setUser(authentication);
                    } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (Objects.nonNull(authentication))
                            log.info("Disconnected Auth : " + authentication.getName());
                        else
                            log.info("Disconnected Sess : " + accessor.getSessionId());
                    }
                }
                return message;
            }
        });
    }
}
