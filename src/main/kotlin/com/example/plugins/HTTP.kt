package com.example.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthScheme
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSyntaxHighlight
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("MyCustomHeader")
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
    install(AutoHeadResponse)
    install(StatusPages) {
        status(HttpStatusCode.BadRequest){call, status ->
            call.respond(status, ErrorMessage(message =status.toString() ))
        }
        status(HttpStatusCode.Unauthorized){call, status ->
            call.respond(status, ErrorMessage(message ="Unauthorized Access" ))
        }
    }
    install(SwaggerUI) {
        swagger {
            forwardRoot = true
            swaggerUrl = "swagger"
            // authentication = "auth-jwt"
            onlineSpecValidator()
            displayOperationId = true
            showTagFilterInput = true
            //sort = SwaggerUiSort.HTTP_METHOD
            syntaxHighlight = SwaggerUiSyntaxHighlight.MONOKAI
        }
        info {
            title = "Api"
            version = "latest"
            description = "My Api"
            termsOfService = "http://example.com/terms"
            contact {
                name = "API Support"
                url = "http://www.example.com/support"
                email = "support@example.com"
            }
            license {
                name = "Apache 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.html"
            }
        }
        server {
            url = "http://localhost:8080"
            description = "Development server"
        }
        server {
            url = "https://example.com/"
            description = "Example server"
        }

        securityScheme("auth-jwt") {
            type = AuthType.HTTP
            scheme = AuthScheme.BEARER
            bearerFormat = "jwt"
        }
        defaultSecuritySchemeName = "auth-jwt"
        schemasInComponentSection = true
   schemas { jsonSchemaBuilder { null }}


    }
    routing {
        openAPI(path = "openapi")

    }

}
