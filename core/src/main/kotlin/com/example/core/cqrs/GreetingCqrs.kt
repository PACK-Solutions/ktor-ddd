package com.example.core.cqrs

import com.example.core.domain.AuditLog
import com.example.core.domain.AuditLogId
import com.example.core.domain.Greeting
import com.example.core.domain.GreetingId
import com.example.core.ports.AuditLogRepository
import com.example.core.ports.GreetingRepository
import com.example.core.ports.TransactionRunner
import com.example.core.ports.SuspendedTransactionRunner

data class GreetingCreated(val id: GreetingId, val messageLength: Int) : DomainEvent

data class CreateGreetingCommand(val message: String) : Command<Greeting>

class CreateGreetingHandler(private val repo: GreetingRepository) : CommandHandler<CreateGreetingCommand, Greeting> {
    override fun handle(command: CreateGreetingCommand): CommandResult<Greeting> {
        val greeting = Greeting(id = GreetingId.random(), message = command.message.trim())
        repo.save(greeting)
        return resultOf(greeting, GreetingCreated(greeting.id, greeting.message.length))
    }
}

class TransactionMiddleware(private val tx: TransactionRunner) : CommandMiddleware {
    override fun <R> invoke(command: Command<R>, next: (Command<R>) -> CommandResult<R>): CommandResult<R> =
        tx.withTransaction { next(command) }
}

class LoggingMiddleware : CommandMiddleware {
    override fun <R> invoke(command: Command<R>, next: (Command<R>) -> CommandResult<R>): CommandResult<R> {
        println("[CommandBus] Handling ${'$'}{command::class.simpleName}")
        val res = next(command)
        println("[CommandBus] Done ${'$'}{command::class.simpleName} events=${'$'}{res.events.size}")
        return res
    }
}

class AuditLogMiddleware(private val auditRepo: AuditLogRepository) : CommandMiddleware {
    override fun <R> invoke(command: Command<R>, next: (Command<R>) -> CommandResult<R>): CommandResult<R> {
        val res = next(command)
        res.events.forEach { evt ->
            when (evt) {
                is GreetingCreated -> {
                    val audit = AuditLog(
                        id = AuditLogId.random(),
                        relatedGreetingId = evt.id,
                        description = "Created greeting with message length=${'$'}{evt.messageLength}",
                    )
                    auditRepo.save(audit)
                }
            }
        }
        return res
    }
}

class AsyncTransactionMiddleware(private val tx: SuspendedTransactionRunner) : AsyncCommandMiddleware {
    override suspend fun <R> invoke(
        command: Command<R>,
        next: suspend (Command<R>) -> CommandResult<R>
    ): CommandResult<R> = tx.withTransaction { next(command) }
}

class AsyncLoggingMiddleware : AsyncCommandMiddleware {
    override suspend fun <R> invoke(
        command: Command<R>,
        next: suspend (Command<R>) -> CommandResult<R>
    ): CommandResult<R> {
        println("[CommandBus-Async] Handling ${'$'}{command::class.simpleName}")
        val res = next(command)
        println("[CommandBus-Async] Done ${'$'}{command::class.simpleName} events=${'$'}{res.events.size}")
        return res
    }
}

class AsyncAuditLogMiddleware(private val auditRepo: AuditLogRepository) : AsyncCommandMiddleware {
    override suspend fun <R> invoke(
        command: Command<R>,
        next: suspend (Command<R>) -> CommandResult<R>
    ): CommandResult<R> {
        val res = next(command)
        res.events.forEach { evt ->
            when (evt) {
                is GreetingCreated -> {
                    val audit = AuditLog(
                        id = AuditLogId.random(),
                        relatedGreetingId = evt.id,
                        description = "Created greeting with message length=${'$'}{evt.messageLength}",
                    )
                    auditRepo.save(audit)
                }
            }
        }
        return res
    }
}

data class GetGreetingQuery(val id: GreetingId) : Query<Greeting?>

class GetGreetingHandler(private val repo: GreetingRepository) : QueryHandler<GetGreetingQuery, Greeting?> {
    override fun handle(query: GetGreetingQuery): Greeting? = repo.findById(query.id)
}
