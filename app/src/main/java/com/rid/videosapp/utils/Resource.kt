package dev.sagar.lifescience.utils

sealed class Resource<out T> {
    data class Loading(val message: String? = "",val progress:Int=0) : Resource<Nothing>()
    data class Success<T>(val response: T, val message: String = "") : Resource<T>()
    data class Error<T>(val error: Throwable?, val message: String = "") : Resource<T>()
}
