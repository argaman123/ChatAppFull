<<<<<<<< HEAD:backend/src/main/kotlin/com/example/demo/config/WebSocketConfig.kt
package com.example.demo.config

import com.example.demo.service.AuthChannelInterceptor
========
package com.example.demo.configs

import com.example.demo.unused.AuthChannelInterceptorAdapter
>>>>>>>> security2:backend/src/main/kotlin/com/example/demo/configs/WebSocketConfig.kt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
<<<<<<<< HEAD:backend/src/main/kotlin/com/example/demo/config/WebSocketConfig.kt
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    @Autowired
    private val channelInterceptor: AuthChannelInterceptor? = null
========
class WebSocketConfig @Autowired constructor(
    private val authChannelInterceptorAdapter: AuthChannelInterceptorAdapter
) : WebSocketMessageBrokerConfigurer {
>>>>>>>> security2:backend/src/main/kotlin/com/example/demo/configs/WebSocketConfig.kt

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
<<<<<<<< HEAD:backend/src/main/kotlin/com/example/demo/config/WebSocketConfig.kt
    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        // Add our interceptor for authentication/authorization
        registration.interceptors(channelInterceptor)
    }
}
========

/*    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.setInterceptors(authChannelInterceptorAdapter)
    }*/

}
>>>>>>>> security2:backend/src/main/kotlin/com/example/demo/configs/WebSocketConfig.kt
