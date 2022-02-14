package com.example.demo.services

import com.example.demo.models.ChatUser
import com.example.demo.models.UserConnectionEvent
import com.example.demo.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


@Component
class ActiveUsersManager @Autowired constructor(
    private val messagingTemplate: SimpMessagingTemplate,
    private val messagesCountManager: MessagesCountManager
    ) {
    val nicknames: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    private fun sendEvent(event :UserConnectionEvent){
        messagingTemplate.convertAndSend("/topic/users", event)
    }

    private fun handleSessionEvent(event :AbstractSubProtocolEvent, type :String) :List<Any>{
        val user = ((event.user as AbstractAuthenticationToken).principal as ChatUser)
        val nickname = user.getNickname()
        val id = user.getEmail() ?: nickname
        sendEvent(UserConnectionEvent(id, nickname, type))
        return listOf(id, nickname, user)
    }

    @EventListener
    private fun handleSessionConnected(event: SessionConnectEvent){
        val details = handleSessionEvent(event, "connected")
        nicknames[details[0] as String] = details[1] as String
        messagesCountManager.addUser(details[2] as ChatUser)
    }

    @EventListener
    private fun handleSessionDisconnected(event: SessionDisconnectEvent){
        val details = handleSessionEvent(event, "disconnected")
        nicknames.remove(details[0])
        messagesCountManager.removeUser(details[2] as ChatUser)
    }

}

//private val connections: ConcurrentLinkedQueue<SseEmitter> = ConcurrentLinkedQueue()
/*fun newEmitter() :SseEmitter {
    val emitter = SseEmitter()
    connections.add(emitter)
    emitter.send(SseEmitter.event().name("all").data(nicknames))
    return emitter
}

private fun broadcast(name :String, data: Any){
    val event = SseEmitter.event().name(name).data(data)
    for (connection in connections)
        connection.send(event)
}
*/
