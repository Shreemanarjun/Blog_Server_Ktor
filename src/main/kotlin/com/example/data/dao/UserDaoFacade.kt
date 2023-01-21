package com.example.data.dao

import com.example.data.UserRequest
import com.example.data.table.User

interface UserDaoFacade {
    suspend fun getAllUser(): List<User>
    suspend fun getUser(id: Int): User?

    suspend fun getUser(user:UserRequest ):Boolean
    suspend fun addNewUser(username: String, password: String): User?
    suspend fun deleteUser(id: Int): Boolean
}