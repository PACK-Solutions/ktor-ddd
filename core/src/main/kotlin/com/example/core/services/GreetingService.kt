package com.example.core.services

import com.example.core.domain.Greeting
import com.example.core.domain.GreetingId
import com.example.core.domain.AuditLog
import com.example.core.domain.AuditLogId
import com.example.core.ports.AuditLogRepository
import com.example.core.ports.GreetingRepository
import com.example.core.ports.TransactionRunner

class GreetingService(
    private val greetingRepo: GreetingRepository,
    private val auditRepo: AuditLogRepository,
    private val tx: TransactionRunner,
) {
    fun create(message: String): Greeting = tx.withTransaction {
        val greeting = Greeting(
            id = GreetingId.random(),
            message = message.trim(),
        )
        greetingRepo.save(greeting)
        // Also save an audit record inside the same transaction
        val audit = AuditLog(
            id = AuditLogId.random(),
            relatedGreetingId = greeting.id,
            description = "Created greeting with message length=${greeting.message.length}",
        )
        auditRepo.save(audit)
        greeting
    }

    fun get(id: GreetingId): Greeting? = tx.withTransaction {
        greetingRepo.findById(id)
    }
}
