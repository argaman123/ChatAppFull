package com.example.demo.controllers

import com.example.demo.entities.User
import com.example.demo.services.JwtUtilService
import com.example.demo.requests.LoginRequest
import com.example.demo.requests.RegisterRequest
import com.example.demo.requests.GuestRequest
import com.example.demo.repositories.UserRepository
import com.example.demo.services.PremiumService
import com.example.demo.services.RealUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

/**
 * This controller can be accessed by anyone, and provides ways to login as a user or a guest, and to register
 */
@RestController
@RequestMapping("auth")
class AuthController @Autowired constructor(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: RealUserDetailsService,
    private val jwtUtilService: JwtUtilService,
    private val passwordEncoder: PasswordEncoder,
    private val premiumService: PremiumService
) {
    /**
     * Tries to authenticate a real user.
     * @param[loginRequest] includes the email and password of the user that tries to log in.
     * @return A [ResponseEntity] that holds a [String].
     *
     * If the login was successful returns the expiration of the JWT and stores it in a http-only cookie.
     * Else, returns the reason the login failed- which is probably because the credentials were incorrect (status 403).
     */
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest, res: HttpServletResponse): ResponseEntity<String> {
        try {
            println(loginRequest)
            authenticationManager.authenticate(loginRequest.getToken())
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(403).body("Incorrect email address or password.")
        }
        val userDetails = userDetailsService.loadUserByUsername(loginRequest.username)
        val jwt = jwtUtilService.generateToken(userDetails.username)
        val cookie = Cookie("jwt", jwt.token)
        cookie.path = "/"
        //cookie.secure = true
        cookie.isHttpOnly = true
        res.addCookie(cookie)
        return ResponseEntity.ok(jwt.expiration)
    }

    /**
     * Tries to authenticate a guest.
     * @param[guestRequest] the nickname of the guest that tries to log in.
     * @return A [ResponseEntity] that holds a [String].
     *
     * If the login was successful returns the expiration of the JWT and stores it in a http-only cookie.
     * Else, returns the reason the login failed- which is probably because the nickname was taken (status 403).
     */
    @PostMapping("/guest")
    fun guest(@RequestBody guestRequest: GuestRequest, res: HttpServletResponse): ResponseEntity<String> {
        try {
            authenticationManager.authenticate(guestRequest.getToken())
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(403).body("Nickname is taken.")
        }
        val jwt = jwtUtilService.generateToken(guestRequest.nickname, "guest")
        val cookie = Cookie("jwt", jwt.token)
        cookie.path = "/"
        //cookie.secure = true
        cookie.isHttpOnly = true
        res.addCookie(cookie)
        return ResponseEntity.ok(jwt.expiration)
    }


    /**
     * Tries to register a new user.
     * @param[registerRequest] includes the email, nickname, password and the requested premium plan of the user.
     * @return A [ResponseEntity] that holds a [String].
     *
     * If the user was successfully registered returns [ResponseEntity.ok].
     * Else, returns all the reasons to why the register failed (status 403).
     */
    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<String> {
        val errors = mutableListOf<String>()
        userRepository.findByEmail(registerRequest.email)?.let {
            errors.add("Email is already being used")
        }
        userRepository.findByNickname(registerRequest.nickname)?.let {
            errors.add("Nickname is already taken")
        }
        return if (errors.isEmpty()) {
            premiumService.switchPlan(
                User(
                    nickname = registerRequest.nickname,
                    email = registerRequest.email,
                    password = passwordEncoder.encode(registerRequest.password),
                    roles = "USER"
                ), registerRequest.premiumPlan
            )
            ResponseEntity.ok("success")
        } else
            ResponseEntity.status(409).body(errors.joinToString(separator = "\n"))
    }

    @GetMapping("/done")
    fun logout() {
    }
}