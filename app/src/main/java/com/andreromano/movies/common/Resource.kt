package com.andreromano.movies.common


sealed class Resource<out DataType>(
    open val data: DataType?,
) {
    data class Loading<out DataType>(override val data: DataType?) : Resource<DataType>(data)

    data class Success<out DataType>(override val data: DataType) : Resource<DataType>(data)

    data class Failure<out DataType>(override val data: DataType?, val error: ErrorKt) : Resource<DataType>(data)

    suspend fun <T> mapData(mapper: suspend (DataType) -> T): Resource<T> = when (this) {
        is Loading -> Loading(data?.let { mapper(it) })
        is Success -> Success(mapper(data))
        is Failure -> Failure(data?.let { mapper(it) }, error)
    }
}