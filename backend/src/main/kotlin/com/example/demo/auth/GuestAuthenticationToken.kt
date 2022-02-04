package com.example.demo.auth

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class GuestAuthenticationToken (private var principal: Any) : AbstractAuthenticationToken(mutableListOf(GrantedAuthority { "GUEST" })) {
    init {
        super.setAuthenticated(true)
    }
    override fun getCredentials(): Any? = null
    override fun getPrincipal(): Any = this.principal
}