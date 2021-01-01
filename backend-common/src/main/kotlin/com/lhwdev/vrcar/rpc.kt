package com.lhwdev.vrcar

import kotlinx.serialization.Serializable


@Serializable
data class Request<T : Any>(val data: T?, val id: Int)


@Serializable
data class Response<T : Any>(val data: T?, val id: Int)
