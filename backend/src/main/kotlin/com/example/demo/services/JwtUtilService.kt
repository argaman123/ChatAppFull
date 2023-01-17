package com.example.demo.services

import com.example.demo.models.ChatUser
import com.example.demo.models.GuestUser
import com.example.demo.static.SECRET_KEY
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * A service that provides ways to either generate a JWT that includes a username and a type of certain [ChatUser],
 * or to extract this information from one.
 */
@Service
class JwtUtilService {

    data class JWT (val token: String, val expiration :String)

    companion object {
        const val TEN_HOURS = 1000 * 60 * 60 * 10
    }

    /**
     * @param[token] a JWT that was generated for a certain [ChatUser].
     * @return all the [Claims] that the token included (I.e., his type and id)
     */
    fun extractAllClaims(token: String): Claims {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body
    }

    /**
     * Uses [createToken] to generate a [JWT] for a user that wishes to connect.
     * @param[username] the nickname (for [GuestUser])
     * @param[type] the type of user: guest/user.
     * Provides a way to separate between tokens that were meant for real users and guests, and it will be later used
     * when generating an [AbstractAuthenticationToken].
     */
    fun generateToken(username :String, type: String = "user"): JWT {
        val claims: HashMap<String, Any> = HashMap()
        claims["type"] = type
        return createToken(claims, username)
    }

    /**
     * @param[claims] the bundled information that will be saved inside the body of the JWT.
     * @param[subject] the username of the user that the JWT is for.
     * @return a bundle of the actual token and its expiration date which can be later sent directly to the user.
     */
    private fun createToken(claims: Map<String, Any>, subject: String): JWT {
        val expiration = Date(System.currentTimeMillis() + TEN_HOURS)
        return JWT(Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact(),
            SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ssZ").format(expiration))

    }

}

