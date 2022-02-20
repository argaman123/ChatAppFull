package com.example.demo.auth

import com.example.demo.models.GuestUser
import com.example.demo.services.ActiveUsersManager
import com.example.demo.services.RealUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class GuestAuthenticationProvider(
    private val realUserDetailsService: RealUserDetailsService,
    private val activeUsersManager: ActiveUsersManager
    ): AuthenticationProvider {

    override fun authenticate(auth: Authentication): Authentication? {
        // probably better to generate a UUID as ID instead of just the nickname
        try {
            realUserDetailsService.loadUserByNickname(auth.name)
        } catch (e: UsernameNotFoundException){
            if (!activeUsersManager.nicknames.values.contains(auth.name))
                return GuestAuthenticationToken(GuestUser(auth.name))
        }
        throw BadCredentialsException("Nickname is taken")
    }

    override fun supports(authenticationType: Class<*>?): Boolean {
        return GuestAuthenticationToken::class.java == authenticationType
    }
}