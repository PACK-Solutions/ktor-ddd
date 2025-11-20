package com.example.core.cqrs

import kotlin.reflect.KClass

interface Command<R>

data class CommandResult<R>(val result: R, val events: List<DomainEvent> = emptyList())

fun <R> resultOf(value: R, vararg events: DomainEvent): CommandResult<R> =
    CommandResult(value, events.toList())

interface CommandHandler<C : Command<R>, R> {
    fun handle(command: C): CommandResult<R>
}

interface CommandMiddleware {
    fun <R> invoke(command: Command<R>, next: (Command<R>) -> CommandResult<R>): CommandResult<R>
}

interface CommandBus {
    fun <R> execute(command: Command<R>): R
}

class SimpleCommandBus(
    handlers: Map<KClass<out Command<*>>, CommandHandler<out Command<*>, *>>,
    middlewares: List<CommandMiddleware> = emptyList(),
) : CommandBus {
    private val handlerMap: Map<KClass<out Command<*>>, CommandHandler<out Command<*>, *>> = handlers
    private val chain: (Command<Any?>) -> CommandResult<Any?>

    init {
        var nextFn: (Command<Any?>) -> CommandResult<Any?> = { cmd -> dispatch(cmd) }
        for (mw in middlewares.asReversed()) {
            val currentNext = nextFn
            nextFn = { cmd -> mw.invoke(cmd, currentNext) }
        }
        chain = nextFn
    }

    @Suppress("UNCHECKED_CAST")
    private fun dispatch(command: Command<Any?>): CommandResult<Any?> {
        val handler = handlerMap[command::class]
            ?: error("No handler registered for command ${'$'}{command::class}")
        @Suppress("UNCHECKED_CAST")
        return (handler as CommandHandler<Command<Any?>, Any?>).handle(command)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R> execute(command: Command<R>): R {
        val res = chain(command as Command<Any?>)
        return res.result as R
    }
}
