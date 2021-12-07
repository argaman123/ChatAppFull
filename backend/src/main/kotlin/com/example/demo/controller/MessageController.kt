package com.example.demo.controller

import com.example.demo.model.Message
import com.example.demo.repository.MessageRepository
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.context.ApplicationListener
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionSubscribeEvent

@Controller
@RestController
class MessageController(
    private val messageRepository: MessageRepository,
) {

    @MessageMapping("/send")
    @SendTo("/topic/chat")
    fun add(message: Message): Message {
        messageRepository.saveAndFlush(message)
        return message
    }

    @SubscribeMapping("/chat")
    fun chatInit() : MutableList<Message> {
        return messageRepository.findAll()
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
