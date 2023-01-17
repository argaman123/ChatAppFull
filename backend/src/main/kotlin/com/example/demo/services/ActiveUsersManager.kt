package com.example.demo.services

import com.example.demo.models.ChatUser
import com.example.demo.models.UserConnectionEvent
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

/**
 * This component handles user connect/disconnect events, and maintains an up-to-date [HashMap] of all the users/guests
 * that are currently connected to the chat- in the following form:
 *
 * id: [String] -> nickname: [String]
 */
@Component
class ActiveUsersManager @Autowired constructor(
    private val messagingTemplate: SimpMessagingTemplate
    ) {
    val nicknames: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    /**
     * Lets all users when a user connects/disconnects.
     * @param[event] holds the id and nickname of the user that will be later added to the [nicknames] map, as well as
     * the type of the event: connected/disconnected.
     */
    private fun sendEvent(event :UserConnectionEvent){
        messagingTemplate.convertAndSend("/topic/users", event)
    }

    /**
     * An abstract way of handling a session connection/disconnection event.
     * Generates a [UserConnectionEvent] and sends it to all users.
     * @param[event] a [SessionConnectEvent] or a [SessionDisconnectEvent], which is used for extracting the user information
     * @param[type] the type of event in a [String] format: connected/disconnected
     * @return the [ChatUser] that was extracted from the [event]
     */
    private fun handleSessionEvent(event :AbstractSubProtocolEvent, type :String) : ChatUser {
        val user = ((event.user as AbstractAuthenticationToken).principal as ChatUser)
        val nickname = user.getNickname()
        val id = user.getID()
        sendEvent(UserConnectionEvent(id, nickname, type))
        return user
    }

    /**
     * Handles a user connection event.
     * Uses [handleSessionEvent] to extract the user information from the [SessionConnectEvent] and adds it to [nicknames]
     */
    @EventListener
    private fun handleSessionConnected(event: SessionConnectEvent){
        val user = handleSessionEvent(event, "connected")
        nicknames[user.getID()] = user.getNickname()
    }

    /**
     * Handles a user disconnection event.
     * Uses [handleSessionEvent] to extract the user information from the [SessionDisconnectEvent] and removes it from [nicknames]
     */
    @EventListener
    private fun handleSessionDisconnected(event: SessionDisconnectEvent){
        val user = handleSessionEvent(event, "disconnected")
        nicknames.remove(user.getID())
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
