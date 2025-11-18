package com.example.infra.exposed

import com.example.core.ports.TransactionRunner
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedTransactionRunner : TransactionRunner {
    override fun <T> withTransaction(block: () -> T): T = transaction { block() }
}
