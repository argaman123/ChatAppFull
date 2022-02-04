package com.example.demo.models

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

data class LoginDTO(val username: String, val password: String){
    fun getToken() = UsernamePasswordAuthenticationToken(username, password)
}
