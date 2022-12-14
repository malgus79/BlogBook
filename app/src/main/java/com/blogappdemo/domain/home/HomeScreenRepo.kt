package com.blogappdemo.domain.home

import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post

interface HomeScreenRepo {

    //metodo para ir a buscar la info al servidor
    suspend fun getLatestPosts(): Result<List<Post>>
    suspend fun registerLikeButtonState(postId: String, liked: Boolean)
    suspend fun registerShareButtonState(postId: String, shared: Boolean)
    suspend fun registerCommentButtonState(postId: String, commented: Boolean)
    suspend fun deleteButtonState(postId: String)
}