package com.example.core.domain

import java.util.UUID

@JvmInline
value class AuditLogId(val value: UUID) {
    override fun toString(): String = value.toString()

    companion object {
        fun random(): AuditLogId = AuditLogId(UUID.randomUUID())
    }
}

/**
 * A simple audit record to demonstrate multi-entity transactional save.
 */
data class AuditLog(
    val id: AuditLogId,
    val relatedGreetingId: GreetingId,
    val description: String,
)
