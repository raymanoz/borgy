package com.raymanoz.borgy.scale

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

class ScalesEndpoints(private val scales: ScalesRepository) : RoutingHttpHandler {

    private val handler: RoutingHttpHandler = routes(
            "/api" bind routes(
                    "/scales" bind routes(
                            "/{name}" bind Method.GET to scale(),
                            "/" bind Method.GET to scales()
                    )
            )

    )

    private fun scale(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            scales.get(name)?.let { scale ->
                Response(Status.OK).body(Jackson.asJsonString(scale))
            } ?: Response(Status.NOT_FOUND).body("No scale found for '$name'")
        } ?: Response(Status.BAD_REQUEST).body("Path parameter 'name' missing")
    }

    private fun scales(): HttpHandler = { _ ->
        Response(Status.OK).body(Jackson.asJsonString(scales.scales()))
    }

    override fun invoke(request: Request): Response = handler(request)

    override fun match(request: Request): HttpHandler? = handler.match(request)

    override fun withBasePath(new: String): RoutingHttpHandler = handler.withBasePath(new)

    override fun withFilter(new: Filter): RoutingHttpHandler = handler.withFilter(new)

}