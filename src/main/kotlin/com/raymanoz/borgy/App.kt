package com.raymanoz.borgy

import com.raymanoz.borgy.scale.ScalesRepository
import com.raymanoz.borgy.trial.Entry
import com.raymanoz.borgy.trial.TrialsRepository
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PATCH
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.asJsonString
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp

class App(private val config: Config,
          private val scales: ScalesRepository, private val trials: TrialsRepository) : HttpHandler {

    private val app = ServerFilters.Cors(UnsafeGlobalPermissive).then(routes(
            "/api" bind routes(
                    "/ping" bind GET to ping(),
                    "/scales" bind routes(
                            "/{name}" bind GET to scale(),
                            "/" bind GET to scales()
                    ),
                    "/trials" bind routes(
                            "/{name}" bind routes(
                                    GET to trial(),
                                    DELETE to completeTrial(),
                                    PATCH to addTrialButtonClick()
                            ),
                            routes(
                                    GET to trials(),
                                    POST to newTrial()
                            )
                    )
            ),
            singlePageApp(config.client)
    ))

    override fun invoke(p1: Request): Response = app(p1)

    private fun ping(): HttpHandler = { Response(OK).body("pong") }

    private fun completeTrial(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            trials.complete(name)
            Response(OK)
        } ?: Response(BAD_REQUEST).body("Path parameter 'name' missing")
    }

    private fun trials(): HttpHandler = {
        Response(OK).body(asJsonString(trials.names()))
    }

    private fun trial(): HttpHandler = { req: Request ->
        req.path("name")?.let { name ->
            trials.get(name)?.let { trial ->
                Response(OK).body(asJsonString(trial))
            } ?: Response(NOT_FOUND).body("No trial found for '$name'")
        } ?: Response(BAD_REQUEST).body("Path parameter 'name' missing")
    }


    private fun newTrial(): HttpHandler = { req ->
        val lens = Body.auto<NewTrial>().toLens()
        val newTrial = lens(req)

        val (name) = trials.newTrial(newTrial.name, newTrial.scale)

        Response(OK).body("""{ "url" : "/trial/$name" } """)
    }

    private fun addTrialButtonClick(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            val elementLens = Body.auto<Entry>().toLens()
            val entry = elementLens(req)
            trials.put(name, entry)?.let {
                Response(OK)
            } ?: Response(NOT_FOUND).body("No trial found for '$name'")
        } ?: Response(BAD_REQUEST).body("Path parameter 'name' missing")
    }

    private fun scale(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            scales.get(name)?.let { scale ->
                Response(OK).body(asJsonString(scale))
            } ?: Response(NOT_FOUND).body("No scale found for '$name'")
        } ?: Response(BAD_REQUEST).body("Path parameter 'name' missing")
    }

    private fun scales(): HttpHandler = { _ ->
        Response(OK).body(asJsonString(scales.scales()))
    }

}
