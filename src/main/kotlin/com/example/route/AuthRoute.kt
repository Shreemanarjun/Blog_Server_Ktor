package com.example.route

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.ErrorMessage
import com.example.data.MyToken
import com.example.data.UserRequest
import com.example.data.dao.user.userDao
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
        val isUserAvailable = userDao.isUserAvailable(user)
        if (isUserAvailable) {
            val audience = this@authRoutes.environment?.config?.property("jwt.audience")?.getString()
            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(this@authRoutes.environment?.config?.property("jwt.domain")?.getString())
                .withClaim("username", user.username)
                .withClaim("tokenType", "accessToken")
                .withExpiresAt(Date(System.currentTimeMillis() + 30000 ))
                .sign(Algorithm.HMAC256("secret"))
            call.respond(MyToken(token = token))
        } else {
            call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Invalid username or password"))
        }
    }




}