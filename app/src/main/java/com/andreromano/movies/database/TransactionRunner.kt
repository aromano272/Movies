package com.andreromano.movies.database

interface TransactionRunner {
    suspend fun run(block: suspend () -> Unit)
}