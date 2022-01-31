package com.example.demo.jwt

import com.example.demo.service.RealUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtAuthFilter @Autowired constructor(
    val jwtUtil: JwtUtil,
    val userDetailsService: RealUserDetailsService
) : OncePerRequestFilter() {

    companion object {
        const val tokenPrefix = "Bearer "
    }

    private val skipFilterUrls: List<String> =
        listOf("/authenticate")

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
            val username = jwtUtil.extractAllClaims(it).subject
            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(username)
                val userAuthToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                userAuthToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = userAuthToken
            }
        }
        filterChain.doFilter(request, response)
    }
}