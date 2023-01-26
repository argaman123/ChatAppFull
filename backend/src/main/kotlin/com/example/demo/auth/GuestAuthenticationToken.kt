package com.example.demo.auth

import com.example.demo.models.GuestUser
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
/**
 * A basic authentication token for guests which stores no credentials and has a rule of GUEST
 * @param[principal] a [GuestUser] or a [String] that will be stored inside the token and used for authentication
 */
class GuestAuthenticationToken (private val principal: Any, private val credentials: Any?) : AbstractAuthenticationToken(mutableListOf(GrantedAuthority { "GUEST" })) {
    init {
        super.setAuthenticated(true)
    }
    override fun getCredentials() = credentials
    override fun getPrincipal() = principal
}