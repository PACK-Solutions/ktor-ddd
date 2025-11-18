package com.example.core.ports

import com.example.core.domain.AuditLog

interface AuditLogRepository {
    fun save(audit: AuditLog)
}
