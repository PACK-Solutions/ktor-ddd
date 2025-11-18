package com.example.infra.exposed

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

data class DbConfig(
    val url: String,
    val driver: String = "org.postgresql.Driver",
    val user: String,
    val password: String,
)

object ExposedDatabaseSupport {
    fun connect(cfg: DbConfig) {
        org.jetbrains.exposed.sql.Database.connect(
            url = cfg.url,
            driver = cfg.driver,
            user = cfg.user,
            password = cfg.password,
        )
    }

    fun initSchema() {
        transaction { SchemaUtils.create(GreetingsTable, AuditLogsTable) }
    }
}
