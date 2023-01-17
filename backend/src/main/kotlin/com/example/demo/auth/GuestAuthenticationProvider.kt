package com.example.demo.auth

import com.example.demo.models.GuestUser
import com.example.demo.services.ActiveUsersManager
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
    private val activeUsersManager: ActiveUsersManager
    ): AuthenticationProvider {

    override fun authenticate(auth: Authentication): Authentication? {
        if (!activeUsersManager.nicknames.values.contains(auth.name)) // -> or a guest
            return GuestAuthenticationToken(GuestUser(auth.name))
        else
            throw BadCredentialsException("Nickname is taken")
    }

    override fun supports(authenticationType: Class<*>?): Boolean {
        return GuestAuthenticationToken::class.java == authenticationType
    }
}