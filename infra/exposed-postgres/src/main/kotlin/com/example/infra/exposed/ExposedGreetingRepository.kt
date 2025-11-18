package com.example.infra.exposed

import com.example.core.domain.Greeting
import com.example.core.domain.GreetingId
import com.example.core.ports.GreetingRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class ExposedGreetingRepository : GreetingRepository {
    override fun save(greeting: Greeting) {
        GreetingsTable.insert { stmt ->
            stmt[GreetingsTable.id] = greeting.id.value
            stmt[GreetingsTable.message] = greeting.message
        }
    }

    override fun findById(id: GreetingId): Greeting? {
        return GreetingsTable
            .selectAll()
            .where { GreetingsTable.id eq id.value }
            .limit(1)
            .firstOrNull()
            ?.toGreeting()
    }

    private fun ResultRow.toGreeting(): Greeting = Greeting(
        id = GreetingId(this[GreetingsTable.id]),
        message = this[GreetingsTable.message],
    )
}
