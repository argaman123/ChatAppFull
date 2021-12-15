package com.example.demo.service

import org.springframework.beans.factory.annotation.Autowired
import com.example.demo.service.UserService
import org.springframework.security.core.userdetails.UserDetailsService
import kotlin.Throws
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import com.example.demo.service.WebSocketAuthenticatorService
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.Message
import org.springframework.stereotype.Service

@Service
class AuthChannelInterceptor @Autowired constructor(private val service: WebSocketAuthenticatorService) :
    ChannelInterceptor {
    // Processes a message before sending it
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {

        // Instantiate an object for retrieving the STOMP headers
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)!!
        // If the frame is a CONNECT frame
        if (accessor.command == StompCommand.CONNECT) {

            // retrieve the username from the headers
            val username = accessor.getFirstNativeHeader(USERNAME_HEADER)
            // retrieve the password from the headers
            val password = accessor.getFirstNativeHeader(PASSWORD_HEADER)
            // authenticate the user and if that's successful add their user information to the headers
            val user = service.getAuthenticatedOrFail(username, password)
            accessor.user = user
        }
        return message
    }

    companion object {
        private const val USERNAME_HEADER = "username"
        private const val PASSWORD_HEADER = "password"
    }
}