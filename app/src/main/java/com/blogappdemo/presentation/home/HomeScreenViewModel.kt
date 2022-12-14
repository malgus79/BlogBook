package com.blogappdemo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post
import com.blogappdemo.domain.home.HomeScreenRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repo: HomeScreenRepoImpl) : ViewModel() {

//    //solicitar info al repo en un hilo secundario (Dispacher.IO)
//    fun fetchLatestPosts() = liveData(Dispatchers.IO) {
//        //emitir valor de carga
//        emit(Result.Loading())
//        try {
//            emit(repo.getLatestPosts())
//        } catch (e: Exception) {
//            emit(Result.Failure(e))
//        }
//    }

    //con Flow coroutine builder
    val latestPosts: StateFlow<Result<List<Post>>> = flow {
        kotlin.runCatching {
            //operacion a ejecutar en el servidor
            repo.getLatestPosts()
        }.onSuccess { postList ->
            emit(postList)
        }.onFailure { throwable ->
            emit(Result.Failure(Exception(throwable)))
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000), // Or Lazily because it's a one-shot
        initialValue = Result.Loading()
    )

//    //sin Flow coroutine builder
//    private val posts = MutableStateFlow<Result<List<Post>>>(Result.Loading())
//    fun fetchPosts() {
//        viewModelScope.launch {
//            kotlin.runCatching {
//                repo.getLatestPosts()
//            }.onFailure { throwable ->
//                posts.value = Result.Failure(Exception(throwable))
//            }.onSuccess { postList ->
//                posts.value = postList
//            }
//        }
//    }
//    fun getPosts(): StateFlow<Result<List<Post>>> = posts


    //Estado de Likes
    fun registerLikeButtonState(postId: String, liked: Boolean) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        kotlin.runCatching {
            repo.registerLikeButtonState(postId, liked)
        }.onSuccess {
            emit(Result.Success(Unit))
        }.onFailure { throwable ->
            emit(Result.Failure(Exception(throwable.message)))
        }
    }

    //Estado de Shares
    fun registerShareButtonState(postId: String, shared: Boolean) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        kotlin.runCatching {
            repo.registerShareButtonState(postId, shared)
        }.onSuccess {
            emit(Result.Success(Unit))
        }.onFailure { throwable ->
            emit(Result.Failure(Exception(throwable.message)))
        }
    }

    //Estado de Comments
    fun registerCommentButtonState(postId: String, commented: Boolean) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        kotlin.runCatching {
            repo.registerCommentButtonState(postId, commented)
        }.onSuccess {
            emit(Result.Success(Unit))
        }.onFailure { throwable ->
            emit(Result.Failure(Exception(throwable.message)))
        }
    }

    fun deletePost(postId: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        kotlin.runCatching {
            repo.deleteButtonState(postId)
        }.onSuccess {
            emit(Result.Success(Unit))
        }.onFailure { throwable ->
            emit(Result.Failure(Exception(throwable.message)))
        }
    }
}