package com.example.demo.controllers

import com.example.demo.entities.Message
import com.example.demo.dao.MessageRepo
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController
import org.springframework.messaging.simp.annotation.SubscribeMapping

@Controller
@RestController
class MessageController(
    private val messageRepository: MessageRepo,
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
