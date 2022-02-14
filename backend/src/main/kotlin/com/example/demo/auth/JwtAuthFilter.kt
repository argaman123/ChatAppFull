package com.example.demo.auth

import com.example.demo.models.GuestUser
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
@Component
class JwtAuthFilter @Autowired constructor(
    val jwtUtil: JwtUtil,
    val userDetailsService: RealUserDetailsService
) : OncePerRequestFilter() {

    companion object {
        const val tokenPrefix = "Bearer "
    }

    private val skipFilterUrls: List<String> = listOf("/auth/**")

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return skipFilterUrls.stream().anyMatch { url: String ->
            AntPathRequestMatcher(
                url
            ).matches(request)
        }
    }
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var token: String? = null
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith(tokenPrefix)) {
            println("header")
            token = header.removePrefix(tokenPrefix)
        } else {
            request.cookies?.let {
                for (cookie in it)
                    if (cookie.name == "jwt") {
                        println("cookie")
                        token = cookie.value
                        break
                    }
            }
        }
        token?.let {
            val claims = jwtUtil.extractAllClaims(it)
            val username = claims.subject // mail for users, nickname for guests
            val type = claims["type"]
            if (SecurityContextHolder.getContext().authentication == null) {
                var userAuthToken :AbstractAuthenticationToken? = null
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