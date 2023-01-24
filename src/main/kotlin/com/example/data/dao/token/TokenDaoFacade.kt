package com.example.data.dao.token

import com.example.data.table.token.Token

enum class TokenType{
    accessToken,refreshToken,allToken
}

interface TokenDaoFacade {
    suspend fun getAllToken():List<Token>

    suspend fun addToken(token: Token):Boolean

    suspend fun replaceAccessToken(userId: Int,accessToken:String):Boolean

    suspend fun isTokenAvailable(userId: Int):Boolean

    suspend fun deleteToken(tokenType:TokenType,userId:Int):Boolean
}