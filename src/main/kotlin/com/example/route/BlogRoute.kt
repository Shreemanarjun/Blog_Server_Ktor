package com.example.route

import com.example.data.ErrorMessage
import com.example.data.dao.blog.blogDao
import com.example.data.table.blog.Blogs
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.patch
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class BlogRequest(val title: String)
data class BlogUpdateRequest(val blogId: Int, val title: String)

fun Routing.blogRoutes() {
    authenticate("auth-jwt") {
        route("/blog") {
            get(
                {
                    tags = listOf("Blogs")
                    response {
                        HttpStatusCode.OK to {
                            description = "Get all Blogs"
                            body<Blogs>()

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
                principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                val blogs = blogDao.getBlogsByUser(userID = userid)
                call.respond(blogs)
            }

            post(
                {
                    tags = listOf("Blogs")
                    request {
                        body<BlogRequest> {
                            description = "Blog Create Request"
                            required = true

                            example("Blog 1", BlogRequest(title = "I am a blog")) {
                                description = "Example for blog request"
                                summary = "Default Blog Request"
                            }
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Get all blogs on this blog created"
                            body<Blogs>()

                        }

                        HttpStatusCode.BadRequest to {
                            description = "Bad Request"
                            body<ErrorMessage> {
                                example("Bad Request Error", ErrorMessage(message = "Unable to create"))
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
                principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                val blogRequest = call.receive<BlogRequest>()
                val isBlogCreated = blogDao.createBlog(userID = userid, title = blogRequest.title)
                if (isBlogCreated) {
                    val blogs = blogDao.getBlogsByUser(userID = userid)
                    call.respond(blogs)
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Unable to crate new blog"))
                }
            }

            patch(
                {
                    tags = listOf("Blogs")
                    request {
                        body<BlogUpdateRequest> {
                            description = "Blog update request"
                            required = true

                            example("Blog Update", BlogUpdateRequest(blogId = 1, title = "Update1")) {
                                description = "Example of Blog Update Request"
                                summary = "Default Blog Update Request"
                            }
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Get all updated blogs on updated"
                            body<Blogs>()

                        }

                        HttpStatusCode.BadRequest to {
                            description = "Bad Request"
                            body<ErrorMessage> {
                                example("Bad Request Error", ErrorMessage(message = "Unable to update"))
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
                principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                val blogupdateRequest = call.receive<BlogUpdateRequest>()
                val isBlogUpdated = blogDao.updateBlog(
                    userID = userid,
                    blogId = blogupdateRequest.blogId,
                    title = blogupdateRequest.title
                )
                if (isBlogUpdated) {
                    val blogs = blogDao.getBlogsByUser(userID = userid)
                    call.respond(blogs)
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Unable to update blog"))
                }
            }
            delete("{id}", {
                tags = listOf("Blogs")
                request {
                    pathParameter<Int>("id") {
                        description = "id of the blog"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Get all updated blogs on updated"
                        body<Blogs>()

                    }

                    HttpStatusCode.NotFound to {
                        description = "Bad Request"
                        body<ErrorMessage> {
                            example("Bad Request Error", ErrorMessage(message = "Unable to delete"))
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
                val principal = call.principal<JWTPrincipal>()
                principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                val blogid = call.parameters["id"]
                if (blogid != null) {
                    try {
                        val intblogid = Integer.parseInt(blogid)
                        val isBlogDeleted = blogDao.deleteBlog(userID = userid, blogId = intblogid)
                        if (isBlogDeleted) {
                            val blogs = blogDao.getBlogsByUser(userID = userid)
                            call.respond(blogs)
                        } else {
                            call.respond(HttpStatusCode.NotFound, ErrorMessage(message = "Unable to delete blog"))
                        }
                    }catch (e:NumberFormatException){
                        call.respond(HttpStatusCode.NotFound, ErrorMessage(message = "ID should be an Integer"))
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "ID should not be null"))
                }

            }
        }
    }
}