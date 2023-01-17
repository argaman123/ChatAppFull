package com.example.demo.controllers

//import com.example.demo.service.ActiveUserManager
import com.example.demo.entities.Message
import com.example.demo.responses.ChatMessageResponse
import com.example.demo.models.ChatUser
import com.example.demo.requests.MessageRequest
import com.example.demo.repositories.MessageRepository
import com.example.demo.services.ActiveUsersManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * This controller handles anything that has to do with the chat itself, and other stuff that require real time updates
 */
@RestController
class ChatController (
    private val messageRepository: MessageRepository,
    private val activeUsersManager: ActiveUsersManager,
    private val messagingTemplate: SimpMessagingTemplate,
) {
    /**
     * @return A [ResponseEntity] which holds a [List] of all the [ChatMessageResponse] from the database
     */
    @GetMapping("/chat/history")
    fun chatInit(): ResponseEntity<List<ChatMessageResponse>> {
        return ResponseEntity.ok(messageRepository.findAll().map { ChatMessageResponse(it) })
    }

    /**
     * @return A [ResponseEntity] which holds a [Map] of all the users that are currently connected to the chat in the following form:
     *
     * id: [String] -> nickname: [String]
     */
    @GetMapping("/chat/users")
    fun usersInit(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(activeUsersManager.nicknames)
    }

    /**
     * Tries to send a message that a user sent to everyone.
     * In case the user has reached the message limit for free users, he would receive a reply at /topic/reply
     * @param[message] the content of the message the user tries to send
     * @return t
     */
    @MessageMapping("/send")
    fun send(auth: Authentication, message: MessageRequest) {
        val user = auth.principal as ChatUser
        val chatMessageResponse = ChatMessageResponse(user.getNickname(), message.content)
        messageRepository.saveAndFlush(Message(chatMessageResponse, user))
        messagingTemplate.convertAndSend("/topic/chat", chatMessageResponse)
    }

}