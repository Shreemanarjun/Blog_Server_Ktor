package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
@Serializable
@SerialName("a")
data class User(val username: String, val password: String)


data class MyToken(val token: String)


data class ErrorMessage(val message:String)

fun Application.configureSecurity() {

    authentication {
        jwt("auth-jwt") {
            val jwtAudience = this@configureSecurity.environment.config.property("jwt.audience").getString()
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256("secret"))
                    .withAudience(jwtAudience)
                    .withIssuer(this@configureSecurity.environment.config.property("jwt.domain").getString())
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username")
                        .asString() == "Arjun"
                ) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
    routing {

        post("/login", {
            tags = listOf("Auth")
            description = "Performs the given operation on the given values and returns the result"
            request {
                body<User> {
                    description = "User with username and password"
                    required = true
                    example("First", User(username = "Arjun", password = "password")) {
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
                    body<ErrorMessage>{
                        example("Bad request",ErrorMessage(message ="Invalid username or password" ))
                    }
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal Server Error"
                    body<ErrorMessage>{
                        example("Internal Server Error",ErrorMessage(message ="Internal Server Error" ))
                    }
                }
            }

        }) {
            val user = call.receive<User>()
            if (user.username == "Arjun" && user.password == "password"
            ) {

                val audience = this@configureSecurity.environment.config.property("jwt.audience").getString()
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(this@configureSecurity.environment.config.property("jwt.domain").getString())
                    .withClaim("username", user.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 30))
                    .sign(Algorithm.HMAC256("secret"))
                call.respond(MyToken(token = token))
            } else {
                call.respond(HttpStatusCode.BadRequest, ErrorMessage(message ="Invalid username or password" ))
            }
        }
    }

}
