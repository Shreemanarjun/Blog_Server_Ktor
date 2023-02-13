package com.example.plugins

import com.example.route.authRoutes
import com.example.route.blogRoutes
import com.example.route.userRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        authRoutes()
        userRoute()
        blogRoutes()
    }

}
