package com.example.demo.service

import com.example.demo.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class WebSocketAuthenticatorService {
    @Autowired
    private val userService: UserService? = null

    @Autowired
    private val passwordEncoder: PasswordEncoder? = null

    @Autowired
    private val authManager: AuthenticationManager? = null
    @Throws(AuthenticationException::class)
    fun getAuthenticatedOrFail(username: String?, password: String?): UsernamePasswordAuthenticationToken {

        // Check the username and password are not empty
        if (username == null || username.trim { it <= ' ' }.isEmpty()) {
            throw AuthenticationCredentialsNotFoundException("Username was null or empty.")
        }
        if (password == null || password.trim { it <= ' ' }.isEmpty()) {
            throw AuthenticationCredentialsNotFoundException("Password was null or empty.")
        }
        // Check that the user with that username exists
        val user: User = userService!!.findUserByUsername(username)
            ?: throw AuthenticationCredentialsNotFoundException("User not found")
        val token = UsernamePasswordAuthenticationToken(
            username,
            password, listOf(SimpleGrantedAuthority(user.authority))
        )

        // verify that the credentials are valid
        authManager?.authenticate(token)

        // Erase the password in the token after verifying it because we will pass it to the STOMP headers.
        token.eraseCredentials()
        return token
    }
}