package com.example.infra.exposed

import org.jetbrains.exposed.sql.Table

object AuditLogsTable : Table("audit_logs") {
    val id = uuid("id").uniqueIndex()
    val greetingId = uuid("greeting_id").index()
    val description = varchar("description", length = 255)
    override val primaryKey = PrimaryKey(id)
}
