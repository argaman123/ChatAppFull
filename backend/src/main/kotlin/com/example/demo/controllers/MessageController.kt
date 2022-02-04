package com.example.demo.controllers

//import com.example.demo.service.ActiveUserManager
import com.example.demo.entities.Message
import com.example.demo.models.ChatMessage
import com.example.demo.models.ChatUser
import com.example.demo.models.MessageDTO
import com.example.demo.models.RealUser
import com.example.demo.repositories.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController @Autowired constructor(
    private val messageRepository: MessageRepository,
) {
    @GetMapping("/chat-history")
    fun chatInit() : ResponseEntity<List<ChatMessage>> {
        return ResponseEntity.ok(messageRepository.findAll().map { ChatMessage(it) })
    }

    @MessageMapping("/send")
    @SendTo("/topic/chat")
    fun add(auth: Authentication, message: MessageDTO): ChatMessage {
        val user =  auth.principal as ChatUser
        val chatMessage = ChatMessage(user.getNickname(), message.content)
        messageRepository.saveAndFlush(Message(chatMessage, user))
        return chatMessage
    }

    @SubscribeMapping("/topic/users")
    @SendTo("/topic/users")
    fun test() :String {
        println("hello")
        return "hello"
    }
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
