package com.example.route

import com.example.data.ErrorMessage
import com.example.data.dao.token.tokenDao
import com.example.data.dao.user.userDao
import com.example.data.table.token.Tokens
import com.example.data.table.user.Users
import io.github.smiley4.ktorswaggerui.dsl.get
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
                    example("tokens", value =Tokens(tokens = listOf()))
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
        route("/user") {
            get(
                "/refreshAccessToken",
                {
                    tags = listOf("User")

                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                val tokentype = principal.payload.getClaim("tokenType").asString()
                val expiresAt = principal.expiresAt?.time

                val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").apply {
                    timeZone = (TimeZone.getTimeZone("Asia/Kolkata"))
                }
                val expiredTime = formatter.format(expiresAt?.let { it1 -> Date(it1) })
                val response = mapOf(
                    "username" to username,
                    "userid" to userid,
                    "tokenType" to tokentype,
                    "expiretime" to expiredTime
                )
                call.respond(response)
            }
        }
    }
}