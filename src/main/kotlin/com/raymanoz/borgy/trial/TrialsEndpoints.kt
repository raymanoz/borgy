package com.raymanoz.borgy.trial

import com.natpryce.asResultOr
import com.natpryce.flatMap
import com.natpryce.get
import com.natpryce.map
import com.raymanoz.borgy.scale.Scale
import com.raymanoz.util.pathResult
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
                            "/{name}/selectPreviousIntensity" bind Method.POST to selectPreviousIntensity(),
                            "/{name}/selectNextIntensity" bind Method.POST to selectNextIntensity(),
                            "/{name}/complete" bind Method.POST to completeTrial(),
                            "/{name}" bind routes(
                                    Method.GET to trial(),
                                    Method.DELETE to archiveTrial()
                            ),
                            routes(
                                    Method.GET to trials(),
                                    Method.POST to newTrial()
                            )
                    )
            )
    )

    private fun archiveTrial(): HttpHandler = { req ->
        req.pathResult("name").map { name ->
            trials.complete(name)
            Response(Status.OK)
        }.get()
    }

    private fun trials(): HttpHandler = {
        Response(Status.OK).with(Body.auto<List<TrialSummary>>().toLens() of
                trials.get().partition { it.state == State.ACTIVE }.toList().flatten().map { it.summary() }
        )
    }

    private fun trial(): HttpHandler = { req ->
        trialFromPathParam(req) { trial ->
            if (req.header("Content-Type") ?: "" == "text/csv") {
                Response(Status.OK).body(toCSV(trial))
            } else {
                Response(Status.OK).with(UiTrial.lens of UiTrial.from(trial, scales))
            }
        }
    }

    private fun toCSV(trial: Trial): String {
        val results = trial.observations.flatMap { observation ->
            observation.events.map { event ->
                Triple(event.time,observation.scaleName,event.intensity)
            }
        }.sortedBy { it.first }.map{"${it.first},${it.second},${it.third}"}
        return "time,scale,intensity\n" + results.joinToString("\n")
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

    private fun selectPreviousIntensity(): HttpHandler = changeIntensity { _, selectedIntensity ->
        (selectedIntensity - 1).coerceAtLeast(0)
    }

    private fun selectNextIntensity(): HttpHandler = changeIntensity { uiObservation, selectedIntensity ->
        (selectedIntensity + 1).coerceAtMost(uiObservation.scale.intensities.size - 1)
    }

    private fun changeIntensity(fn: (UiObservation, Int) -> Int): HttpHandler = { req ->
        trialFromPathParam(req) { trial ->
            val selectedObservation = trial.observations[trial.selectedObservation]
            val uiObservation = UiObservation.from(selectedObservation, scales)
            val newIndex = uiObservation.selectedIntensity?.let { fn(uiObservation, it) } ?: 0

            val newObservations = trial.observations.map { observation ->
                if (observation.scaleName == uiObservation.scale.name)
                    observation.copy(events = observation.events + Event(Instant.now(), uiObservation.scale.intensities[newIndex].number))
                else
                    observation
            }
            val newTrial = trials.put(trial.copy(observations = newObservations))
            Response(Status.OK).with(UiTrial.lens of UiTrial.from(newTrial, scales))
        }
    }

    private fun completeTrial(): HttpHandler = { req ->
        trialFromPathParam(req) { trial ->
            val newTrial = trials.put(trial.copy(state = State.COMPLETE))
            Response(Status.OK).with(UiTrial.lens of UiTrial.from(newTrial, scales))
        }
    }

    private fun newTrial(): HttpHandler = { req ->
        val newTrial = (Body.auto<NewTrial>().toLens())(req)
        val name = trials.newTrial(newTrial.name, newTrial.scales).name
        Response(Status.OK).body("""{ "url" : "/trial/$name" } """)
    }

    override fun invoke(request: Request): Response = handler(request)

    override fun match(request: Request): HttpHandler? = handler.match(request)

    override fun withBasePath(new: String): RoutingHttpHandler = handler.withBasePath(new)

    override fun withFilter(new: Filter): RoutingHttpHandler = handler.withFilter(new)

    private fun trial(name: String) =
            trials.get(name).asResultOr { Response(Status.NOT_FOUND).body("No trial found for '$name'") }

    private fun trialFromPathParam(req: Request, fn: (Trial) -> Response): Response =
            req.pathResult("name").flatMap { name ->
                trial(name).map { trial -> fn(trial) }
            }.get()
}