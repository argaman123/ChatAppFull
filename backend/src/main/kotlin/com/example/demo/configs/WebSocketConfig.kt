package com.example.demo.configs

import com.example.demo.unused.AuthChannelInterceptorAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig @Autowired constructor(
    private val authChannelInterceptorAdapter: AuthChannelInterceptorAdapter
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            .addEndpoint("/chat-connection")
            .setAllowedOrigins("http://localhost:4200")
            .withSockJS()
    }


/*    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.setInterceptors(authChannelInterceptorAdapter)
    }*/

}