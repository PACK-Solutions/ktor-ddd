package com.example

import com.example.core.cqrs.*
import com.example.core.ports.GreetingRepository
import com.example.core.ports.TransactionRunner
import com.example.core.ports.SuspendedTransactionRunner
import com.example.infra.exposed.DbConfig
import com.example.infra.exposed.ExposedDatabaseSupport
import com.example.infra.exposed.ExposedGreetingRepository
import com.example.infra.exposed.ExposedTransactionRunner
import com.example.infra.exposed.ExposedSuspendedTransactionRunner
import com.example.core.ports.AuditLogRepository
import com.example.infra.exposed.ExposedAuditLogRepository
import io.ktor.server.application.*
import kotlin.reflect.KClass

fun Application.configureFrameworks() {
    // Read DB configuration
    val cfg = environment.config
    val dbUrl = cfg.propertyOrNull("db.url")?.getString()
        ?: "jdbc:postgresql://localhost:5432/ktor_ddd"
    val dbUser = cfg.propertyOrNull("db.user")?.getString() ?: "postgres"
    val dbPassword = cfg.propertyOrNull("db.password")?.getString() ?: "postgres"

    ExposedDatabaseSupport.connect(DbConfig(url = dbUrl, user = dbUser, password = dbPassword))
    ExposedDatabaseSupport.initSchema()
    val tx: TransactionRunner = ExposedTransactionRunner()
    val asyncTx: SuspendedTransactionRunner = ExposedSuspendedTransactionRunner()
    val greetingRepo: GreetingRepository = ExposedGreetingRepository()
    val auditRepo: AuditLogRepository = ExposedAuditLogRepository()

    val commandHandlers: Map<KClass<out Command<*>>, CommandHandler<out Command<*>, *>> = mapOf(
        CreateGreetingCommand::class to CreateGreetingHandler(greetingRepo)
    )
    val queryHandlers: Map<KClass<out Query<*>>, QueryHandler<out Query<*>, *>> = mapOf(
        GetGreetingQuery::class to GetGreetingHandler(greetingRepo)
    )

    val middlewares: List<CommandMiddleware> = listOf(
        TransactionMiddleware(tx),
        AuditLogMiddleware(auditRepo),
        LoggingMiddleware(),
    )

    ServiceRegistry.commandBus = SimpleCommandBus(commandHandlers, middlewares)
    val asyncMiddlewares: List<AsyncCommandMiddleware> = listOf(
        AsyncTransactionMiddleware(asyncTx),
        AsyncLoggingMiddleware(),
        AsyncAuditLogMiddleware(auditRepo),
    )
    ServiceRegistry.asyncCommandBus = SimpleAsyncCommandBus(commandHandlers, asyncMiddlewares)
    ServiceRegistry.queryBus = SimpleQueryBus(queryHandlers)
}

object ServiceRegistry {
    lateinit var commandBus: CommandBus
    lateinit var asyncCommandBus: AsyncCommandBus
    lateinit var queryBus: QueryBus
}
