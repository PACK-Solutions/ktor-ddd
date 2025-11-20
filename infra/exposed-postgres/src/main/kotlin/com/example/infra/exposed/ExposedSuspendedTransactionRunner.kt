package com.example.infra.exposed

import com.example.core.ports.SuspendedTransactionRunner
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ExposedSuspendedTransactionRunner : SuspendedTransactionRunner {
    override suspend fun <T> withTransaction(block: suspend () -> T): T = newSuspendedTransaction { block() }
}
