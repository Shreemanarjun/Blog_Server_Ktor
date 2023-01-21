package com.example.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthScheme
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSyntaxHighlight
import io.ktor.server.application.*

fun Application.configureSwagger(){
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
    }
}