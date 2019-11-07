package com.raymanoz.borgy.scale

import com.natpryce.flatMap
import com.natpryce.get
import com.natpryce.map
import com.raymanoz.util.pathResult
import com.raymanoz.borgy.scale.Scale.Companion.scalesLens
import com.raymanoz.util.maybeToEither
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
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
        req.pathResult("name").flatMap { name ->
            scale(name).map { scale ->
                Response(Status.OK).with(Scale.lens of scale)
            }
        }.get()
    }

    private fun scale(name: String) =
            scales.get(name).maybeToEither(Response(Status.NOT_FOUND).body("No scale found for '$name'"))


    private fun scales(): HttpHandler = { _ ->
        Response(Status.OK).with(scalesLens of scales.scales())
    }

    override fun invoke(request: Request): Response = handler(request)

    override fun match(request: Request): HttpHandler? = handler.match(request)

    override fun withBasePath(new: String): RoutingHttpHandler = handler.withBasePath(new)

    override fun withFilter(new: Filter): RoutingHttpHandler = handler.withFilter(new)

}