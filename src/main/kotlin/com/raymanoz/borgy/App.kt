package com.raymanoz.borgy

import com.raymanoz.borgy.scale.ScalesEndpoints
import com.raymanoz.borgy.trial.TrialsEndpoints
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp

class App(config: Config, trialsEndpoints: TrialsEndpoints, scalesEndpoints: ScalesEndpoints) : HttpHandler {

    private val app = ServerFilters.Cors(UnsafeGlobalPermissive).then(routes(
            "/api/ping" bind GET to ping(),
            scalesEndpoints,
            trialsEndpoints,
            singlePageApp(config.client)
    ))

    override fun invoke(p1: Request): Response = app(p1)

    private fun ping(): HttpHandler = { Response(OK).body("pong") }

}
