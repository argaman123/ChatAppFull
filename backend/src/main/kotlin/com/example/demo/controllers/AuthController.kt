package com.example.demo.controllers

import com.example.demo.entities.User
import com.example.demo.services.JwtUtilService
import com.example.demo.requests.GuestRequest
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
    private val authenticationManager: AuthenticationManager,
    private val jwtUtilService: JwtUtilService
) {
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

    @GetMapping("/done")
    fun logout() {
    }
}