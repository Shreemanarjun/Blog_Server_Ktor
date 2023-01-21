package com.example.plugins

import com.example.data.dao.userDao
import com.example.route.authRoutes
import com.example.route.userRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.text.SimpleDateFormat
import java.util.*

fun Application.configureRouting() {

    routing {
        authRoutes()
        userRoute()
    }

}
