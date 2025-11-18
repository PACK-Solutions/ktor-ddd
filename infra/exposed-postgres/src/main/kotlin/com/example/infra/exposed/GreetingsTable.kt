package com.example.infra.exposed

import org.jetbrains.exposed.sql.Table

object GreetingsTable : Table("greetings") {
    val id = uuid("id").uniqueIndex()
    val message = varchar("message", length = 255)
    override val primaryKey = PrimaryKey(id)
}
