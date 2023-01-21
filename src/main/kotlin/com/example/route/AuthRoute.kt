package com.example.route

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.ErrorMessage
import com.example.data.MyToken
import com.example.data.UserRequest
import com.example.data.dao.userDao
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.text.SimpleDateFormat
import java.util.*

fun Routing.authRoutes() {
    post("/login", {
        tags = listOf("Auth")
        description = "Performs the given operation on the given values and returns the result"
        request {
            body<UserRequest> {
                description = "User with username and password"
                required = true

                example("First", UserRequest(username = "Arjun", password = "password")) {
                    description = "A longer description of the example"
                    summary = "Default login credential to get token"
                }
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "The operation was successful"
                body<MyToken>()

            }
            HttpStatusCode.BadRequest to {
                description = "Something went wrong"
                body<ErrorMessage> {
                    example("Bad request", ErrorMessage(message = "Invalid username or password"))
                }
            }
            HttpStatusCode.InternalServerError to {
                description = "Internal Server Error"
                body<ErrorMessage> {
                    example("Internal Server Error", ErrorMessage(message = "Internal Server Error"))
                }
            }
        }

    }) {
        val user = call.receive<UserRequest>()
        val isUserAvailable = userDao.getUser(user)
        if (isUserAvailable) {
            val audience = this@authRoutes.environment?.config?.property("jwt.audience")?.getString()
            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(this@authRoutes.environment?.config?.property("jwt.domain")?.getString())
                .withClaim("username", user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 30))
                .sign(Algorithm.HMAC256("secret"))
            call.respond(MyToken(token = token))
        } else {
            call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Invalid username or password"))
        }
    }

    authenticate("auth-jwt") {
        get("/hello") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time

            val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")

            val expiredTime = formatter.format(expiresAt?.let { it1 -> Date(it1) })
            val json = mapOf("expireAtDate" to expiredTime, "username" to username)

            call.respond(message = json, status = HttpStatusCode.OK)

        }

        get("/ok") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            call.respondText { "Hey$username Authenticated.... :)" }
        }
    }
}