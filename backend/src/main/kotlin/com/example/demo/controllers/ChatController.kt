package com.example.demo.controllers

//import com.example.demo.service.ActiveUserManager
import com.example.demo.entities.Message
import com.example.demo.models.ChatMessage
import com.example.demo.models.ChatUser
import com.example.demo.models.MessageDTO
import com.example.demo.repositories.MessageRepository
import com.example.demo.services.ActiveUsersManager
import com.example.demo.services.MessagesCountManager
import com.example.demo.static.freeUserMessageLimit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController @Autowired constructor(
    private val messageRepository: MessageRepository,
    private val activeUsersManager: ActiveUsersManager,
    private val messagesCountManager: MessagesCountManager,
    private val messagingTemplate: SimpMessagingTemplate
) {
    @GetMapping("/chat/history")
    fun chatInit(): ResponseEntity<List<ChatMessage>> {
        return ResponseEntity.ok(messageRepository.findAll().map { ChatMessage(it) })
    }

    @GetMapping("/chat/users")
    fun usersInit(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(activeUsersManager.nicknames)
    }

    @MessageMapping("/send")
    fun send(auth: Authentication, message: MessageDTO) {
        val user = auth.principal as ChatUser
        if (!user.isPremium() && messagesCountManager.getCount(user) >= freeUserMessageLimit) {
            messagingTemplate.convertAndSendToUser(
                user.getID(),
                "/topic/reply",
                ChatMessage(user.getNickname(), "Reached the massage limit for free users (${freeUserMessageLimit})", type = "alert")
            )
        } else {
            messagesCountManager.increaseCounter(user)
            val chatMessage = ChatMessage(user.getNickname(), message.content)
            messageRepository.saveAndFlush(Message(chatMessage, user))
            messagingTemplate.convertAndSend("/topic/chat", chatMessage)
        }
    }


    /*@GetMapping("/users", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getUsers() :SseEmitter {
        return activeUsersService.newEmitter()
    }*/
}
/*

@Component
class SubscribeListener (
    private val messagingTemplate: SimpMessagingTemplate,
    private val messageRepository: MessageRepository,
) : ApplicationListener<SessionSubscribeEvent> {
    override fun onApplicationEvent(event: SessionSubscribeEvent) {
        messagingTemplate.convertAndSendToUser(event.user!!.name, "/api/chat", messageRepository.findAll())
    }
}*/
