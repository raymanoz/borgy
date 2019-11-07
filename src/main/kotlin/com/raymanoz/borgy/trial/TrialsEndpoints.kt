package com.raymanoz.borgy.trial

import com.natpryce.flatMap
import com.natpryce.get
import com.natpryce.map
import com.raymanoz.util.pathResult
import com.raymanoz.borgy.scale.Scale
import com.raymanoz.util.maybeToEither
import org.http4k.core.Body
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
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
        req.pathResult("name").map { name ->
            trials.complete(name)
            Response(Status.OK)
        }.get()
    }

    private fun trials(): HttpHandler = {
        Response(Status.OK).with(Body.auto<List<String>>().toLens() of trials.names())
    }

    private fun trial(): HttpHandler = { req ->
        trialFromPathParam(req) { trial ->
            Response(Status.OK).with(UiTrial.lens of UiTrial.from(trial, scales))
        }
    }

    private fun selectPreviousObservation(): HttpHandler = { req ->
        trialFromPathParam(req) { trial ->
            val newTrial = trial.selectPrevious()
            trials.put(newTrial)
            Response(Status.OK).with(UiTrial.lens of UiTrial.from(newTrial, scales))
        }
    }

    private fun selectNextObservation(): HttpHandler = { req ->
        trialFromPathParam(req) { trial ->
            val newTrial = trial.selectNext()
            trials.put(newTrial)
            Response(Status.OK).with(UiTrial.lens of UiTrial.from(newTrial, scales))
        }
    }

    private fun newTrial(): HttpHandler = { req ->
        val newTrial = (Body.auto<NewTrial>().toLens())(req)
        val name = trials.newTrial(newTrial.name, newTrial.scales).name
        Response(Status.OK).body("""{ "url" : "/trial/$name" } """)
    }

    private fun logEvent(): HttpHandler = { req ->
        trialFromPathParam(req) { trial ->
            val elementLens = Body.auto<UiEvent>().toLens()
            val uiEvent = elementLens.extract(req)
            val newObservations = trial.observations.map { observation ->
                if (observation.scaleName == uiEvent.scale)
                    observation.copy(events = observation.events + Event(Instant.now(), uiEvent.intensity))
                else
                    observation
            }
            val newTrial = trials.put(trial.copy(observations = newObservations))
            Response(Status.OK).with(UiTrial.lens of UiTrial.from(newTrial, scales))
        }
    }

    override fun invoke(request: Request): Response = handler(request)

    override fun match(request: Request): HttpHandler? = handler.match(request)

    override fun withBasePath(new: String): RoutingHttpHandler = handler.withBasePath(new)

    override fun withFilter(new: Filter): RoutingHttpHandler = handler.withFilter(new)

    private fun trial(name: String) =
            trials.get(name).maybeToEither(Response(Status.NOT_FOUND).body("No trial found for '$name'"))

    private fun trialFromPathParam(req: Request, fn: (Trial) -> Response): Response =
            req.pathResult("name").flatMap { name ->
                trial(name).map { trial -> fn(trial) }
            }.get()
}