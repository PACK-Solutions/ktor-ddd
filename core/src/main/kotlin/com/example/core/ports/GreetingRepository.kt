package com.example.core.ports

import com.example.core.domain.Greeting
import com.example.core.domain.GreetingId

interface GreetingRepository {
    fun save(greeting: Greeting)
    fun findById(id: GreetingId): Greeting?
}
