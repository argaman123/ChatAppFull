package com.example.demo.auth

import com.example.demo.models.GuestUser
import com.example.demo.services.ActiveUsersManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

/**
 * A special authentication provider for [GuestUser] which accepts all requests as long as their nickname is unique.
 */
@Component
class GuestAuthenticationProvider(
    @Value("\${global.password:123123}") private val globalPassword: String,
    private val activeUsersManager: ActiveUsersManager
    ): AuthenticationProvider {

    override fun authenticate(auth: Authentication): Authentication? {
        if (globalPassword == auth.credentials)
            if (!activeUsersManager.nicknames.values.contains(auth.name))
                return GuestAuthenticationToken(GuestUser(auth.name), auth.credentials)
            else
                throw BadCredentialsException("Nickname is taken")
        else
            throw BadCredentialsException("Password is incorrect")
    }

    override fun supports(authenticationType: Class<*>?): Boolean {
        return GuestAuthenticationToken::class.java == authenticationType
    }
}