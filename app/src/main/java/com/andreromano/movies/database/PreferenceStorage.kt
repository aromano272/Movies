/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andreromano.movies.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.andreromano.movies.common.Millis
import com.andreromano.movies.database.model.ApiConfigurationEntity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var apiConfiguration: ApiConfigurationEntity?
    var lastApiConfigurationSync: Millis

    var nowPlayingMoviesPagingNextRemoteKey: Int?
    var popularMoviesPagingNextRemoteKey: Int?
    var topRatedMoviesPagingNextRemoteKey: Int?
    var upcomingMoviesPagingNextRemoteKey: Int?

    fun clearAll()
}

/**
 * [PreferenceStorage] impl backed by [android.content.SharedPreferences].
 */
class SharedPreferenceStorage constructor(
    context: Context,
    moshi: Moshi,
) : PreferenceStorage {

    private val prefs = context.getSharedPreferences("Movies", MODE_PRIVATE)

    override var apiConfiguration: ApiConfigurationEntity? by JsonPreference(
        prefs,
        "apiConfiguration",
        moshi.adapter(ApiConfigurationEntity::class.java),
        null,
    )

    override var lastApiConfigurationSync: Millis by NonnullLongPreference(
        prefs,
        "lastApiConfigurationSync",
        0L,
    )

    override var nowPlayingMoviesPagingNextRemoteKey: Int? by IntPreference(
        prefs,
        "nowPlayingMoviesPagingNextRemoteKey",
        null,
    )

    override var popularMoviesPagingNextRemoteKey: Int? by IntPreference(
        prefs,
        "popularMoviesPagingNextRemoteKey",
        null,
    )

    override var topRatedMoviesPagingNextRemoteKey: Int? by IntPreference(
        prefs,
        "topRatedMoviesPagingNextRemoteKey",
        null,
    )

    override var upcomingMoviesPagingNextRemoteKey: Int? by IntPreference(
        prefs,
        "upcomingMoviesPagingNextRemoteKey",
        null,
    )

    override fun clearAll() {
        prefs.edit().clear().commit()
    }
}

class BooleanPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Boolean,
) : ReadWriteProperty<Any, Boolean> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.edit().putBoolean(name, value).commit()
    }
}

class StringPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: String?,
) : ReadWriteProperty<Any, String?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.edit().putString(name, value).commit()
    }
}

class NonnullStringSetPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Set<String>,
) : ReadWriteProperty<Any, Set<String>> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Set<String> {
        return preferences.getStringSet(name, defaultValue)!!
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Set<String>) {
        preferences.edit().putStringSet(name, value).commit()
    }
}

class LongPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Long?,
) : ReadWriteProperty<Any, Long?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Long? {
        return if (preferences.contains(name)) {
            val value = preferences.getLong(name, Long.MIN_VALUE)
            return if (value != Long.MIN_VALUE) {
                value
            } else {
                defaultValue
            }
        } else {
            defaultValue
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long?) {
        if (value != null) {
            preferences.edit().putLong(name, value).apply()
        } else {
            preferences.edit().remove(name).apply()
        }
    }
}

class NonnullLongPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Long,
) : ReadWriteProperty<Any, Long> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return if (preferences.contains(name)) {
            val value = preferences.getLong(name, Long.MIN_VALUE)
            return if (value != Long.MIN_VALUE) {
                value
            } else {
                defaultValue
            }
        } else {
            defaultValue
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.edit().putLong(name, value).apply()
    }
}

class IntPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Int?,
) : ReadWriteProperty<Any, Int?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Int? {
        return if (preferences.contains(name)) {
            val value = preferences.getInt(name, Int.MIN_VALUE)
            return if (value != Int.MIN_VALUE) {
                value
            } else {
                defaultValue
            }
        } else {
            defaultValue
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int?) {
        if (value != null) {
            preferences.edit().putInt(name, value).commit()
        } else {
            preferences.edit().remove(name).commit()
        }
    }
}

class DoublePreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Double?,
) : ReadWriteProperty<Any, Double?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Double? {
        return if (preferences.contains(name)) {
            val value = preferences.getFloat(name, Float.MIN_VALUE)
            return if (value != Float.MIN_VALUE) {
                value.toDouble()
            } else {
                defaultValue
            }
        } else {
            defaultValue
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Double?) {
        if (value != null) {
            preferences.edit().putFloat(name, value.toFloat()).commit()
        } else {
            preferences.edit().remove(name).commit()
        }
    }
}

class FloatPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Float?,
) : ReadWriteProperty<Any, Float?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Float? {
        return if (preferences.contains(name)) {
            val value = preferences.getFloat(name, Float.MIN_VALUE)
            return if (value != Float.MIN_VALUE) {
                value
            } else {
                defaultValue
            }
        } else {
            defaultValue
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Float?) {
        if (value != null) {
            preferences.edit().putFloat(name, value).commit()
        } else {
            preferences.edit().remove(name).commit()
        }
    }
}

class JsonPreference<T>(
    private val preferences: SharedPreferences,
    private val name: String,
    private val adapter: JsonAdapter<T>,
    private val defaultValue: T,
) : ReadWriteProperty<Any, T?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return preferences.getString(name, null)?.let { adapter.fromJson(it) } ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        if (value != null) {
            preferences.edit().putString(name, adapter.toJson(value)).commit()
        } else {
            preferences.edit().remove(name).commit()
        }
    }
}
