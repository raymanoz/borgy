package com.raymanoz.borgy.trial

import com.raymanoz.borgy.NewTrial
import org.http4k.core.Body
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

class TrialsEndpoints(private val trials: TrialsRepository) : RoutingHttpHandler {

    private val handler = routes(
            "/api" bind routes(
                    "/trials" bind routes(
                            "/{name}" bind routes(
                                    Method.GET to trial(),
                                    Method.DELETE to completeTrial(),
                                    Method.PATCH to recordState()
                            ),
                            routes(
                                    Method.GET to trials(),
                                    Method.POST to newTrial()
                            )
                    )
            )
    )

    private fun completeTrial(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            trials.complete(name)
            Response(Status.OK)
        } ?: Response(Status.BAD_REQUEST).body("Path parameter 'name' missing")
    }

    private fun trials(): HttpHandler = {
        Response(Status.OK).body(Jackson.asJsonString(trials.names()))
    }

    private fun trial(): HttpHandler = { req: Request ->
        req.path("name")?.let { name ->
            trials.get(name)?.let { trial ->
                Response(Status.OK).body(Jackson.asJsonString(trial))
            } ?: Response(Status.NOT_FOUND).body("No trial found for '$name'")
        } ?: Response(Status.BAD_REQUEST).body("Path parameter 'name' missing")
    }


    private fun newTrial(): HttpHandler = { req ->
        val lens = Body.auto<NewTrial>().toLens()
        val newTrial = lens(req)

        val (name) = trials.newTrial(newTrial.name, newTrial.scales)

        Response(Status.OK).body("""{ "url" : "/trial/$name" } """)
    }

    private fun recordState(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            val elementLens = Body.auto<State>().toLens()
            trials.put(name, elementLens(req))?.let {
                Response(Status.OK)
            } ?: Response(Status.NOT_FOUND).body("No trial found for '$name'")
        } ?: Response(Status.BAD_REQUEST).body("Path parameter 'name' missing")
    }

    override fun invoke(request: Request): Response = handler(request)

    override fun match(request: Request): HttpHandler? = handler.match(request)

    override fun withBasePath(new: String): RoutingHttpHandler = handler.withBasePath(new)

    override fun withFilter(new: Filter): RoutingHttpHandler = handler.withFilter(new)

}