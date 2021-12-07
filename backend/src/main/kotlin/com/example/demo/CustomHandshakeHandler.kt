package com.example.demo

import org.springframework.http.server.ServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal
import java.util.*

class StompPrincipal(private var name: String) : Principal {
    override fun getName(): String {
        return name
    }
}

class CustomHandshakeHandler : DefaultHandshakeHandler() {
    override fun determineUser(
        request: ServerHttpRequest,
        wsHandler: WebSocketHandler,
        attributes: Map<String, Any>
    ): Principal {
        return StompPrincipal(UUID.randomUUID().toString())
    }
}