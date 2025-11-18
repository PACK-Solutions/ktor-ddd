package com.example.core.domain

import java.util.UUID

@JvmInline
value class GreetingId(val value: UUID) {
    override fun toString(): String = value.toString()

    companion object {
        fun random(): GreetingId = GreetingId(UUID.randomUUID())
        fun parse(text: String): GreetingId = GreetingId(UUID.fromString(text))
    }
}

data class Greeting(
    val id: GreetingId,
    val message: String,
)
