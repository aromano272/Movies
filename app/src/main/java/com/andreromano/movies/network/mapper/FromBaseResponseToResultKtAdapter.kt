package com.andreromano.movies.network.mapper

import com.andreromano.movies.common.ErrorKt
import com.andreromano.movies.common.ResultKt
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class FromBaseResponseToResultKtAdapter<T>(private val adapter: JsonAdapter<T>) : JsonAdapter<ResultKt<T>>() {
    override fun fromJson(reader: JsonReader): ResultKt<T> {
        val response = adapter.fromJson(reader)

        // Custom response parsing goes here
        return when {
            response == null -> ResultKt.Failure(ErrorKt.Generic)
            else -> ResultKt.Success(response)
        }
    }

    override fun toJson(writer: JsonWriter, value: ResultKt<T>?) {
        throw UnsupportedOperationException()
    }
}