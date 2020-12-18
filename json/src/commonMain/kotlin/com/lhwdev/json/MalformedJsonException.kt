package com.lhwdev.json


class MalformedJsonException(
    message: String? = null,
    cause: Throwable? = null
) : IllegalStateException(message, cause)