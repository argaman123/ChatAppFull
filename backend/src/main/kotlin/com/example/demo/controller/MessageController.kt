package com.example.demo.controller

//import com.example.demo.service.ActiveUserManager
import com.example.demo.entities.Message
import com.example.demo.jwt.JwtUtil
import com.example.demo.models.AuthenticationRequest
import com.example.demo.models.ChatMessage
import com.example.demo.models.MessageDTO
import com.example.demo.models.RealUser
import com.example.demo.repository.MessageRepository
import com.example.demo.repository.UserRepository
import com.example.demo.service.RealUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse


@Controller
@RestController
class MessageController @Autowired constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: RealUserDetailsService,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody authenticationRequest: AuthenticationRequest, res :HttpServletResponse): ResponseEntity<String> {
        try {
            authenticationManager.authenticate(authenticationRequest.getToken())
        } catch (e :BadCredentialsException){
            throw Exception("Incorrect credentials", e)
        }
        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
        val jwt = jwtUtil.generateToken(userDetails)
        val cookie = Cookie("jwt", jwt.token)
        cookie.path = "/"
        //cookie.secure = true
        cookie.isHttpOnly = true
        res.addCookie(cookie)
        return ResponseEntity.ok(jwt.expiration)
    }

    @GetMapping("/chat-history")
    fun chatInit() : ResponseEntity<List<ChatMessage>> {
        return ResponseEntity.ok(messageRepository.findAll().map { ChatMessage(it) })
    }

    @MessageMapping("/send")
    @SendTo("/topic/chat")
    fun add(auth: Authentication, message: MessageDTO): ChatMessage {
        val user =  auth.principal as RealUser
        val chatMessage = ChatMessage(user.nickname, message.content)
        messageRepository.saveAndFlush(Message(chatMessage, user))
        return chatMessage
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
