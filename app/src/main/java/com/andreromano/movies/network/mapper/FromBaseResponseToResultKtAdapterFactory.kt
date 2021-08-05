package com.andreromano.movies.network.mapper


import com.andreromano.movies.common.ResultKt
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FromBaseResponseToResultKtAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        if (rawType == ResultKt::class.java && type is ParameterizedType) {
            val subType = type.actualTypeArguments.first()

            val adapter: JsonAdapter<Any> = moshi.adapter(subType)

            return FromBaseResponseToResultKtAdapter(adapter)
        }
        return null
    }
}