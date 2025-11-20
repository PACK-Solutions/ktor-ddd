package com.example.core.ports

interface SuspendedTransactionRunner {
    suspend fun <T> withTransaction(block: suspend () -> T): T
}
