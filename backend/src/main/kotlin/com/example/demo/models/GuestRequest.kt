package com.example.demo.models

import com.example.demo.auth.GuestAuthenticationToken

data class GuestRequest(val nickname :String) {
    fun getToken() = GuestAuthenticationToken(nickname)
}