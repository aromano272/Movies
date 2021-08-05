package com.andreromano.movies.database

import androidx.room.*


abstract class BaseDao<in T : Any> {
    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     * @return The SQLite row id
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOrIgnore(obj: @JvmSuppressWildcards T): Long

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     * @return The SQLite row ids
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOrIgnore(obj: List<@JvmSuppressWildcards T>): List<Long>

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    abstract suspend fun update(obj: @JvmSuppressWildcards T)

    /**
     * Update an array of objects from the database.
     *
     * @param obj the objects to be updated
     */
    @Update
    abstract suspend fun update(obj: List<@JvmSuppressWildcards T>)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    abstract suspend fun delete(obj: @JvmSuppressWildcards T)

    /**
     * Delete an array of objects from the database
     *
     * @param obj the objects to be deleted
     */
    @Delete
    abstract suspend fun delete(obj: List<@JvmSuppressWildcards T>)

    /**
     * Upsert an object in the database
     *
     * @param obj the object to be upserted
     */
    @Transaction
    open suspend fun upsert(obj: @JvmSuppressWildcards T) {
        val id = insertOrIgnore(obj)
        if (id == -1L) {
            update(obj)
        }
    }

    /**
     * Upsert an array of objects in the database
     *
     * @param obj the objects to be upserted
     */
    @Transaction
    open suspend fun upsert(objList: List<@JvmSuppressWildcards T>) {
        val insertResult = insertOrIgnore(objList)
        val updateList = insertResult.mapIndexedNotNull { index, value ->
            if (value == -1L) objList[index] else null
        }

        if (updateList.isNotEmpty()) {
            update(updateList)
        }
    }
}