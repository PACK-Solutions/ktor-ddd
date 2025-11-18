package com.example

import com.example.core.ports.GreetingRepository
import com.example.core.ports.TransactionRunner
import com.example.core.services.GreetingService
import com.example.infra.exposed.DbConfig
import com.example.infra.exposed.ExposedDatabaseSupport
import com.example.infra.exposed.ExposedGreetingRepository
import com.example.infra.exposed.ExposedTransactionRunner
import com.example.core.ports.AuditLogRepository
import com.example.infra.exposed.ExposedAuditLogRepository
import io.ktor.server.application.*

fun Application.configureFrameworks() {
    // Read DB configuration
    val cfg = environment.config
    val dbUrl = cfg.propertyOrNull("db.url")?.getString()
        ?: "jdbc:postgresql://localhost:5432/ktor_ddd"
    val dbUser = cfg.propertyOrNull("db.user")?.getString() ?: "postgres"
    val dbPassword = cfg.propertyOrNull("db.password")?.getString() ?: "postgres"

    // Initialize Exposed database and schema
    ExposedDatabaseSupport.connect(DbConfig(url = dbUrl, user = dbUser, password = dbPassword))
    ExposedDatabaseSupport.initSchema()

    // Simple manual wiring without leaking technical deps into core
    val tx: TransactionRunner = ExposedTransactionRunner()
    val greetingRepo: GreetingRepository = ExposedGreetingRepository()
    val auditRepo: AuditLogRepository = ExposedAuditLogRepository()
    ServiceRegistry.greetingService = GreetingService(greetingRepo, auditRepo, tx)
}

object ServiceRegistry {
    lateinit var greetingService: GreetingService
}
