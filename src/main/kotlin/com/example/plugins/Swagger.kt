package com.example.plugins

import com.example.data.ErrorMessage
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthScheme
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSort
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSyntaxHighlight
import io.ktor.server.application.*
import io.ktor.server.engine.*

fun Application.configureSwagger() {
    val engineenv = (environment as ApplicationEngineEnvironment)
    val envHost = System.getenv("RAILWAY_STATIC_URL")
    val envPort = engineenv.config.port
    val engineconnectors = engineenv.connectors

    install(SwaggerUI) {

        swagger {
            forwardRoot = true
            swaggerUrl = "swagger"
            // authentication = "auth-jwt"
            onlineSpecValidator()

            displayOperationId = true
            showTagFilterInput = true
            sort = SwaggerUiSort.HTTP_METHOD
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
        if (envHost != null) {
            server {
                url = "https://${envHost}"
                description = "Development server"
            }
        }
        server {
            url = "https://${engineenv.config.host}:$envPort"
            description = "Development server"
        }
        engineconnectors.forEach { e ->
            server {
                url = "http://${e.host}:${e.port}"
                description = "Development server"
            }
        }



        securityScheme("authJWT") {
            type = AuthType.API_KEY
            scheme = AuthScheme.BEARER
            bearerFormat = "jwt"
        }
        defaultSecuritySchemeName = "authJWT"
        schemasInComponentSection = true
        examplesInComponentSection = true
        defaultUnauthorizedResponse {
            description = "Unauthorized Access"
            body<ErrorMessage> {
                example("Unauthorized Access", ErrorMessage(message = "Unauthorized Access"))
            }
        }


    }
}