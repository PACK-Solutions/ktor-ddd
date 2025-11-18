package com.example

import com.example.core.domain.GreetingId
import com.example.core.services.GreetingService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    val service: GreetingService = ServiceRegistry.greetingService

    routing {
        get("/greetings/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText("Missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
            val greeting = service.get(GreetingId.parse(id))
            if (greeting == null) call.respond(io.ktor.http.HttpStatusCode.NotFound)
            else call.respond(GreetingDTO.fromDomain(greeting))
        }

        post("/greetings") {
            val req = call.receive<CreateGreetingRequest>()
            val created = service.create(req.message)
            call.respond(GreetingDTO.fromDomain(created))
        }
    }
}

@Serializable
data class CreateGreetingRequest(val message: String)

@Serializable
data class GreetingDTO(val id: String, val message: String) {
    companion object {
        fun fromDomain(g: com.example.core.domain.Greeting) = GreetingDTO(
            id = g.id.toString(),
            message = g.message,
        )
    }
}
