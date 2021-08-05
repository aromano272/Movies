package com.andreromano.movies.database.mapper

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@ProvidedTypeConverter
class RoomTypeConverters(
    private val moshi: Moshi,
) {

    @TypeConverter
    fun fromListOfStringsToJson(strings: List<String>?): String? {
        if (strings == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(type)
        return adapter.toJson(strings)
    }

    @TypeConverter
    fun toJsonToListOfStrings(json: String?): List<String>? {
        if (json == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(type)
        return adapter.fromJson(json) ?: emptyList()
    }
}