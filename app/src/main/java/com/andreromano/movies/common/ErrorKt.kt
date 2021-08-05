package com.andreromano.movies.common

/**
 * Represents errors throughout the app, this should in theory be split into domain, network and database specific classes.
 * However, this is placed in common for the sake of convenience of having [ResultKt] and [Resource] not take in a ErrorType and having [ErrorKt] directly
 */
sealed class ErrorKt : Throwable() {
    data class Unknown(override val message: String) : ErrorKt()
    object Unauthorized : ErrorKt()
    object Generic : ErrorKt()
    object NotFound : ErrorKt()
    object Network : ErrorKt()
}