package com.example.demo.controllers

//import com.example.demo.service.ActiveUserManager
import com.example.demo.entities.Message
import com.example.demo.responses.ChatMessageResponse
import com.example.demo.models.ChatUser
import com.example.demo.requests.MessageRequest
import com.example.demo.responses.NotificationResponse
import com.example.demo.repositories.MessageRepository
import com.example.demo.repositories.NotificationRepository
import com.example.demo.services.ActiveUsersManager
import com.example.demo.services.MessagesCountManager
import com.example.demo.services.NotificationService
import com.example.demo.static.freeUserMessageLimit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * This controller handles anything that has to do with the chat itself, and other stuff that require real time updates
 */
@RestController
class ChatController @Autowired constructor(
    private val messageRepository: MessageRepository,
    private val activeUsersManager: ActiveUsersManager,
    private val messagesCountManager: MessagesCountManager,
    private val messagingTemplate: SimpMessagingTemplate,
    private val notificationRepository: NotificationRepository,
    private val notificationService: NotificationService

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
     * @return A [ResponseEntity] which holds a [List] of all the [NotificationResponse] of a certain user.
     */
    @GetMapping("/chat/notifications")
    fun getNotifications(auth: Authentication): ResponseEntity<List<NotificationResponse>>{
        val user = auth.principal as ChatUser
        return ResponseEntity.ok(user.getEmail()?.let { notificationRepository.findByEmail(it).map { n -> NotificationResponse(n) } })
    }

    /**
     * @param[id] the id of the notification that the user wishes to delete
     * @return A [ResponseEntity] which holds a [String] that represents whether the deletion process was successful or not.
     *
     * For example, if the notification is locked or the user tries to delete a notification that doesn't exist or is someone else's.
     */
    @DeleteMapping("/chat/notification/{id}")
    fun deleteNotification(auth: Authentication, @PathVariable id: Long): ResponseEntity<String>{
        val user = auth.principal as ChatUser
        return if (user.getEmail()?.let { notificationService.tryDeletingNotification(it, id) } == true){
            ResponseEntity.ok("Notification deleted successfully")
        } else {
            ResponseEntity.status(403).body("Notification deletion has failed")
        }
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
        if (!user.isPremium() && messagesCountManager.getCount(user) >= freeUserMessageLimit) {
            messagingTemplate.convertAndSendToUser(
                user.getID(),
                "/topic/reply",
                ChatMessageResponse(user.getNickname(), "Reached the massage limit for free users (${freeUserMessageLimit})", type = "alert")
            )
        } else {
            messagesCountManager.increaseCounter(user)
            val chatMessageResponse = ChatMessageResponse(user.getNickname(), message.content)
            messageRepository.saveAndFlush(Message(chatMessageResponse, user))
            messagingTemplate.convertAndSend("/topic/chat", chatMessageResponse)
        }
    }

}