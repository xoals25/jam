package com.tang.chat.config;

import static com.tang.chat.common.constant.ChatConstants.DESTINATION_PREFIX;

import com.tang.chat.application.InboundInterceptor;
import com.tang.chat.common.exception.StompExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final InboundInterceptor inboundInterceptor;

  private final StompExceptionHandler stompExceptionHandler;


  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker(DESTINATION_PREFIX);
    registry.setApplicationDestinationPrefixes("/chats");

  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .setErrorHandler(stompExceptionHandler)
        .addEndpoint("/ws")
//        .setAllowedOriginPatterns("*")
//        .withSockJS();
        .setAllowedOrigins("*")
    ;
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(inboundInterceptor);
  }

  @Bean
  public ServletServerContainerFactoryBean createWebSocketContainer() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    container.setMaxTextMessageBufferSize(8192);
    container.setMaxBinaryMessageBufferSize(8192);
    return container;
  }
}
