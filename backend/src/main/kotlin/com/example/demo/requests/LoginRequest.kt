package com.example.demo.requests

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

data class LoginRequest(val username: String, val password: String){
    fun getToken() = UsernamePasswordAuthenticationToken(username, password)
}
