package com.example.demo.requests

import com.example.demo.auth.GuestAuthenticationToken

data class GuestRequest(val nickname :String, val password: String) {
    fun getToken() = GuestAuthenticationToken(nickname, password)
}