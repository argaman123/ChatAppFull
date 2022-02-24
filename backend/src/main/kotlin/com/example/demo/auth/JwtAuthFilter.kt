package com.example.demo.auth

import com.example.demo.configs.WebSecurityConfig.Companion.unauthorizedURLS
import com.example.demo.models.GuestUser
import com.example.demo.services.JwtUtilService
import com.example.demo.services.RealUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/*
Seems like Sessions would be a better solution for this type of project, since we already keep track of connected users,
but since I wanted to experience with different types of web securities and this one worked first, I kept it.
 */
/**
 * Filters all incoming requests based on the validation of their JWT.
 */
@Component
class JwtAuthFilter @Autowired constructor(
    val jwtUtilService: JwtUtilService,
    val userDetailsService: RealUserDetailsService
) : OncePerRequestFilter() {

    companion object {
        const val tokenPrefix = "Bearer "
        const val tokenHeader = "Authorization"
    }

    // Skips jwt filtering on these following links
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return unauthorizedURLS.any { url -> AntPathRequestMatcher(url).matches(request) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Filter only when you must
        if (SecurityContextHolder.getContext().authentication == null) {
            var token: String? = null
            val header = request.getHeader(tokenHeader)
            // Looks for the JWT in the header ->
            if (header != null && header.startsWith(tokenPrefix)) {
                println("header")
                token = header.removePrefix(tokenPrefix)
            } else { // or in the cookies
                request.cookies?.let {
                    for (cookie in it)
                        if (cookie.name == "jwt") {
                            println("cookie")
                            token = cookie.value
                            break
                        }
                }
            }
            // If found, try to generate an Authentication Token based on its type
            token?.let {
                val claims = jwtUtilService.extractAllClaims(it)
                val username = claims.subject // mail for users, nickname for guests
                val type = claims["type"]
                var userAuthToken: AbstractAuthenticationToken? = null
                if (type == "user") {
                    val userDetails = userDetailsService.loadUserByUsername(username) // change to id maybe?
                    userAuthToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                } else if (type == "guest") {
                    userAuthToken = GuestAuthenticationToken(GuestUser(username))
                }
                userAuthToken?.let { authToken ->
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}