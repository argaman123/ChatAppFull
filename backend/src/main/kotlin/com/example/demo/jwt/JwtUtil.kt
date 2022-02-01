package com.example.demo.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KFunction1


@Service
class JwtUtil {

    data class JWT (val token: String, val expiration :String)

    companion object {
        const val TEN_HOURS = 1000 * 60 * 60 * 10
    }

    // TODO : MOVE THIS OUT OF HERE
    private val SECRET_KEY = "secret"

    fun extractAllClaims(token: String): Claims {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body
    }

    fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }

    fun generateToken(userDetails: UserDetails): JWT {
        val claims: Map<String, Any> = HashMap()
        return createToken(claims, userDetails.username)
    }

    private fun createToken(claims: Map<String, Any>, subject: String): JWT {
        val expiration = Date(System.currentTimeMillis() + TEN_HOURS)
        return JWT(Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact(), SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ssZ").format(expiration))

    }

}
