package com.example.route

import com.example.data.dao.token.tokenDao
import com.example.data.dao.user.userDao
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
        call.respondText("Hello World! Arjuns")
    }
    get("/allUser") {
        call.respond(mapOf("users" to userDao.getAllUser()))
    }
    get("/allTokens"){
        call.respond(mapOf("tokens" to tokenDao.getAllToken()))
    }

    authenticate("auth-jwt") {
        get("/hello") {
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

        get("/ok") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            call.respondText { "Hey$username Authenticated.... :)" }
        }
    }
}