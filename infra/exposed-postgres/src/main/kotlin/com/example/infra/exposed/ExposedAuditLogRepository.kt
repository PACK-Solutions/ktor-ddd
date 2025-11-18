package com.example.infra.exposed

import com.example.core.domain.AuditLog
import com.example.core.ports.AuditLogRepository
import org.jetbrains.exposed.sql.insert

class ExposedAuditLogRepository : AuditLogRepository {
    override fun save(audit: AuditLog) {
        AuditLogsTable.insert { stmt ->
            stmt[AuditLogsTable.id] = audit.id.value
            stmt[AuditLogsTable.greetingId] = audit.relatedGreetingId.value
            stmt[AuditLogsTable.description] = audit.description
        }
    }
}
