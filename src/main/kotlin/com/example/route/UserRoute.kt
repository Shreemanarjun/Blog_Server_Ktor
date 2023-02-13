package com.example.route

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.ErrorMessage
import com.example.data.MyToken
import com.example.data.dao.token.tokenDao
import com.example.data.dao.user.userDao
import com.example.data.table.token.Tokens
import com.example.data.table.user.Users
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.text.SimpleDateFormat
import java.util.*

fun Routing.userRoute() {
    get("/name") {
        call.respondText("Hello World!")
    }
    get(
        "/allUser",
        {
            tags = listOf("default")
            response {
                HttpStatusCode.OK to {
                    description = "The operation was successful"
                    body<Users>()

                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal Server Error"
                    body<ErrorMessage> {
                        example("Internal Server Error", ErrorMessage(message = "Internal Server Error"))
                    }
                }
            }
        },
    ) {
        call.respond(Users(users = userDao.getAllUser()))
    }
    get("/allTokens", {
        tags = listOf("default")
        response {
            HttpStatusCode.OK to {
                description = "The operation was successful"
                body<Tokens> {
                    example("tokens", value = Tokens(tokens = listOf()))
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
        call.respond(
            Tokens(tokens = tokenDao.getAllToken())
        )
    }

    authenticate("auth-jwt") {
        route("/user") {
            get(
                "/hello",
                {
                    tags = listOf("User")

                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val expiresAt = principal.expiresAt?.time

                val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").apply {
                    timeZone = (TimeZone.getTimeZone("Asia/Kolkata"))
                }
                val expiredTime = formatter.format(expiresAt?.let { it1 -> Date(it1) })
                val json = mapOf("expireAtDate" to expiredTime, "username" to username)

                call.respond(message = json, status = HttpStatusCode.OK)

            }

            get(
                "/ok",
                {
                    tags = listOf("User")
                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                call.respondText { "Hey$username Authenticated.... :)" }
            }
        }
    }




    authenticate("auth-refresh-jwt") {
        val audience = this@userRoute.environment?.config?.property("jwt.audience")?.getString()
        val issuer = this@userRoute.environment?.config?.property("jwt.domain")?.getString()
        val secret = this@userRoute.environment?.config?.property("jwt.secret")?.getString()
        route("/user") {
            post(
                "/refreshAccessToken",
                {
                    tags = listOf("User")
                    response {
                        HttpStatusCode.OK to {
                            description = "The operation was successful"
                            body<MyToken>()

                        }
                        HttpStatusCode.BadRequest to {
                            description = "Bad Request"
                            body<ErrorMessage> {
                                example("Bad Request", ErrorMessage(message = "Error in refreshing token"))
                            }
                        }
                        HttpStatusCode.InternalServerError to {
                            description = "Internal Server Error"
                            body<ErrorMessage> {
                                example("Internal Server Error", ErrorMessage(message = "Internal Server Error"))
                            }
                        }
                    }

                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                principal.payload.getClaim("tokenType").asString()
                val expiresAt = principal.expiresAt?.time

                val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").apply {
                    timeZone = (TimeZone.getTimeZone("Asia/Kolkata"))
                }

                formatter.format(expiresAt?.let { it1 -> Date(it1) })

                if (principal.expiresAt?.after(Date()) == true) {
                    val accessToken = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("userid", userid)
                        .withClaim("username", username)
                        .withClaim("tokenType", "accessToken")
                        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                        .sign(Algorithm.HMAC256(secret))
                    val isReplaced = tokenDao.replaceAccessToken(userId = userid, accessToken)
                    if (isReplaced) {
                        val token = tokenDao.getTokens(userid)
                        if (token != null) {
                            token.accessToken?.let { it1 -> MyToken(accessToken= it1, refreshToken = token.refreshToken) }
                                ?.let { it2 -> call.respond(it2) }
                        } else {
                            call.respond(
                                status = HttpStatusCode.BadRequest,
                                ErrorMessage(message = "Failed to get token")
                            )
                        }
                    } else {
                        call.respond(status = HttpStatusCode.BadRequest, ErrorMessage(message = "Failed replace token"))
                    }
                } else {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        ErrorMessage(message = "Failed to get token")
                    )
                }
            }
        }
    }
}