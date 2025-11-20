package com.example.core.cqrs

import kotlin.reflect.KClass

interface Query<R>

interface QueryHandler<Q : Query<R>, R> {
    fun handle(query: Q): R
}

interface QueryMiddleware {
    fun <R> invoke(query: Query<R>, next: (Query<R>) -> R): R
}

interface QueryBus {
    fun <R> ask(query: Query<R>): R
}

class SimpleQueryBus(
    private val handlers: Map<KClass<out Query<*>>, QueryHandler<out Query<*>, *>>,
    middlewares: List<QueryMiddleware> = emptyList(),
) : QueryBus {
    private val chain: (Query<Any?>) -> Any?

    init {
        var nextFn: (Query<Any?>) -> Any? = { q -> dispatch(q) }
        for (mw in middlewares.asReversed()) {
            val currentNext = nextFn
            nextFn = { q -> mw.invoke(q, currentNext) }
        }
        chain = nextFn
    }

    @Suppress("UNCHECKED_CAST")
    private fun dispatch(query: Query<Any?>): Any? {
        val handler = handlers[query::class]
            ?: error("No handler registered for query ${'$'}{query::class}")
        @Suppress("UNCHECKED_CAST")
        return (handler as QueryHandler<Query<Any?>, Any?>).handle(query)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R> ask(query: Query<R>): R = chain(query as Query<Any?>) as R
}
