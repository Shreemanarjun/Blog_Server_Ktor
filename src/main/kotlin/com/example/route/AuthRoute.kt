package com.example.route

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.*
import com.example.data.dao.token.TokenType
import com.example.data.dao.token.tokenDao
import com.example.data.dao.user.userDao
import com.example.data.table.token.Token
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Routing.authRoutes() {
    val audience = this@authRoutes.environment?.config?.property("jwt.audience")?.getString()
    val issuer = this@authRoutes.environment?.config?.property("jwt.domain")?.getString()
    val secret = this@authRoutes.environment?.config?.property("jwt.secret")?.getString()
    post(
        "/login",
        {
            tags = listOf("Auth")
            description = "Performs the given operation on the given values and returns the result"
            request {
                body<UserLoginRequest> {
                    description = "User with username and password"
                    required = true

                    example("First", UserLoginRequest(username = "Arjun", password = "password")) {
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

        },
    ) {
        val userLoginRequest = call.receive<UserLoginRequest>()
        val user = userDao.getUser(userLoginRequest)
        when {
            user != null -> {
                val isTokenAvailable = user.id?.let { it1 -> tokenDao.isTokenAvailable(userId = it1) }
                when {
                    isTokenAvailable != null && isTokenAvailable == true -> {
                        tokenDao.deleteToken(tokenType = TokenType.allToken, userId = user.id)
                        val accessToken = JWT.create()
                            .withAudience(audience)
                            .withIssuer(issuer)
                            .withClaim("userid", user.id)
                            .withClaim("username", userLoginRequest.username)
                            .withClaim("tokenType", "accessToken")
                            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                            .sign(Algorithm.HMAC256(secret))
                        val refreshToken = JWT.create()
                            .withAudience(audience)
                            .withIssuer(issuer)
                            .withClaim("userid", user.id)
                            .withClaim("username", userLoginRequest.username)
                            .withClaim("tokenType", "refreshToken")
                            .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 30))
                            .sign(Algorithm.HMAC256(secret))
                        tokenDao.addToken(Token(id = user.id, accessToken = accessToken, refreshToken = refreshToken))
                        call.respond(MyToken(accessToken = accessToken, refreshToken = refreshToken))
                    }
                    else -> {
                        when {
                            user.id != null -> {
                                val accessToken = JWT.create()
                                    .withAudience(audience)
                                    .withIssuer(issuer)
                                    .withClaim("userid", user.id)
                                    .withClaim("username", userLoginRequest.username)
                                    .withClaim("tokenType", "accessToken")
                                    .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                                    .sign(Algorithm.HMAC256(secret))
                                val refreshToken = JWT.create()
                                    .withAudience(audience)
                                    .withIssuer(issuer)
                                    .withClaim("userid", user.id)
                                    .withClaim("username", userLoginRequest.username)
                                    .withClaim("tokenType", "refreshToken")
                                    .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 30))
                                    .sign(Algorithm.HMAC256(secret))
                                tokenDao.addToken(Token(id = user.id, accessToken = accessToken, refreshToken = refreshToken))
                                call.respond(MyToken(accessToken = accessToken, refreshToken = refreshToken))
                            }
                            else -> {
                                call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Invalid username or password"))
                            }
                        }
                    }
                }

            }
            else -> call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Invalid username or password"))
        }
    }

    post("/signup",   {
        tags = listOf("Auth")
        description = "Performs the given operation on the given values and returns the result"
        request {
            body<UserSignUpRequest> {
                description = "User with username and password"
                required = true

                example("First", UserSignUpRequest(username = "Arjun", password = "password")) {
                    description = "A longer description of the example"
                    summary = "Default login credential to get token"
                }
            }
        }
        response {
            HttpStatusCode.Created to {
                description = "The operation was successful"
                body<SuccessMessage> {
                    example("Success", SuccessMessage(message = "Successfully Signed Up"))
                }

            }
            HttpStatusCode.BadRequest to {
                description = "Something went wrong"
                body<ErrorMessage> {
                    example("Bad request", ErrorMessage(message = "Unable to Signup "))
                    example("User name reserved",ErrorMessage(message = "Unable to signup due to username already reserved"))
                }
            }
            HttpStatusCode.InternalServerError to {
                description = "Internal Server Error"
                body<ErrorMessage> {
                    example("Internal Server Error", ErrorMessage(message = "Internal Server Error"))
                }
            }
        }

    },) {
        val userSignUpRequest = call.receive<UserSignUpRequest>()
        val isUserNameExist= userDao.getUser(username = userSignUpRequest.username)
        when {
            isUserNameExist!=null -> {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    ErrorMessage(message = "Unable to signup due to username already reserved")
                )
            }
            else -> {
                try {
                    val isSignedUp =
                        userDao.addNewUser(username = userSignUpRequest.username, password = userSignUpRequest.password)
                    when {
                        isSignedUp != null -> {
                            call.respond(status = HttpStatusCode.Created, SuccessMessage(message = "Successfully signed up"))
                        }
                        else -> {
                            call.respond(status = HttpStatusCode.BadRequest, ErrorMessage(message = "Unable to signup"))
                        }
                    }
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        ErrorMessage(message = "Unable to signup due to ${e.localizedMessage}")
                    )
                }
            }
        }
    }

}