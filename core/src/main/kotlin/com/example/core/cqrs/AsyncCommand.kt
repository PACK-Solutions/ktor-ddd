package com.example.core.cqrs

import kotlin.reflect.KClass

interface AsyncCommandMiddleware {
    suspend fun <R> invoke(
        command: Command<R>,
        next: suspend (Command<R>) -> CommandResult<R>
    ): CommandResult<R>
}

interface AsyncCommandBus {
    suspend fun <R> execute(command: Command<R>): R
}

class SimpleAsyncCommandBus(
    handlers: Map<KClass<out Command<*>>, CommandHandler<out Command<*>, *>>,
    middlewares: List<AsyncCommandMiddleware> = emptyList(),
) : AsyncCommandBus {
    private val handlerMap: Map<KClass<out Command<*>>, CommandHandler<out Command<*>, *>> = handlers
    private val chain: suspend (Command<Any?>) -> CommandResult<Any?>

    init {
        var nextFn: suspend (Command<Any?>) -> CommandResult<Any?> = { cmd -> dispatch(cmd) }
        for (mw in middlewares.asReversed()) {
            val currentNext = nextFn
            nextFn = { cmd -> mw.invoke(cmd, currentNext) }
        }
        chain = nextFn
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun dispatch(command: Command<Any?>): CommandResult<Any?> {
        val handler = handlerMap[command::class]
            ?: error("No handler registered for command ${'$'}{command::class}")
        @Suppress("UNCHECKED_CAST")
        return (handler as CommandHandler<Command<Any?>, Any?>).handle(command)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <R> execute(command: Command<R>): R {
        val res = chain(command as Command<Any?>)
        return res.result as R
    }
}
