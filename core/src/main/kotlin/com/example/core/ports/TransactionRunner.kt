package com.example.core.ports

interface TransactionRunner {
    fun <T> withTransaction(block: () -> T): T
}
