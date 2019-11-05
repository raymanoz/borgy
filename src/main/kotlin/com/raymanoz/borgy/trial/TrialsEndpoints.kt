package com.raymanoz.borgy.trial

import com.raymanoz.borgy.NewTrial
import com.raymanoz.borgy.scale.Scale
import org.http4k.core.Body
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import java.time.Instant

class TrialsEndpoints(private val trials: TrialsRepository, private val scales: List<Scale>) : RoutingHttpHandler {

    private val handler = routes(
            "/api" bind routes(
                    "/trials" bind routes(
                            "/{name}/selectPreviousObservation" bind Method.POST to selectPreviousObservation(),
                            "/{name}/selectNextObservation" bind Method.POST to selectNextObservation(),
                            "/{name}/event" bind Method.POST to logEvent(),
                            "/{name}" bind routes(
                                    Method.GET to trial(),
                                    Method.DELETE to completeTrial()
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
        Response(Status.OK).with(Body.auto<List<String>>().toLens() of trials.names())
    }

    private fun trial(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            trials.get(name)?.let { trial ->
                Response(Status.OK).with(UiTrial.lens of UiTrial.from(trial,scales))
            } ?: Response(Status.NOT_FOUND).body("No trial found for '$name'")
        } ?: Response(Status.BAD_REQUEST).body("Path parameter 'name' missing")
    }

    private fun selectPreviousObservation(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            trials.get(name)?.let { trial ->
                val newTrial = trial.selectPrevious()
                trials.put(newTrial)
                Response(Status.OK).with(UiTrial.lens of UiTrial.from(newTrial, scales))
            } ?: Response(Status.NOT_FOUND).body("No trial found for '$name'")
        } ?: Response(Status.BAD_REQUEST).body("Path parameter 'name' missing")
    }

    private fun selectNextObservation(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            trials.get(name)?.let { trial ->
                val newTrial = trial.selectNext()
                trials.put(newTrial)
                Response(Status.OK).with(UiTrial.lens of UiTrial.from(newTrial, scales))
            } ?: Response(Status.NOT_FOUND).body("No trial found for '$name'")
        } ?: Response(Status.BAD_REQUEST).body("Path parameter 'name' missing")
    }

    private fun newTrial(): HttpHandler = { req ->
        val lens = Body.auto<NewTrial>().toLens()
        val newTrial = lens(req)

        val (name) = trials.newTrial(newTrial.name, newTrial.scales)

        Response(Status.OK).body("""{ "url" : "/trial/$name" } """)
    }

    private fun logEvent(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            val elementLens = Body.auto<UiEvent>().toLens()
            trials.get(name)?.let { trial ->
                val uiEvent = elementLens.extract(req)
                val newObservations = trial.observations.map { observation ->
                    if (observation.scaleName == uiEvent.scale)
                        observation.copy(events = observation.events + Event(Instant.now(), uiEvent.intensity))
                    else
                        observation
                }
                val newTrial = trials.put(trial.copy(observations = newObservations))
                Response(Status.OK).body(Jackson.asJsonString(UiTrial.from(newTrial, scales)))
            } ?: Response(Status.NOT_FOUND).body("No trial found for '$name'")
        } ?: Response(Status.BAD_REQUEST).body("Path parameter 'name' missing")
    }

    override fun invoke(request: Request): Response = handler(request)

    override fun match(request: Request): HttpHandler? = handler.match(request)

    override fun withBasePath(new: String): RoutingHttpHandler = handler.withBasePath(new)

    override fun withFilter(new: Filter): RoutingHttpHandler = handler.withFilter(new)

}