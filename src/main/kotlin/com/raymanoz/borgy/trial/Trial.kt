package com.raymanoz.borgy.trial

import com.raymanoz.borgy.scale.Scale
import org.http4k.core.Body
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import java.lang.RuntimeException
import java.time.Instant
import kotlin.math.min
import kotlin.math.max

data class NewTrial(val name: String, val scales: List<String>)

enum class State {
    ACTIVE, COMPLETE
}

data class Trial(val name: String, val observations: List<Observation>, val selectedObservation: Int = 0, val state: State = State.ACTIVE) {
    companion object {
        val lens: BiDiBodyLens<Trial> = Body.auto<Trial>().toLens()
    }

    fun selectNext(): Trial = copy(
            selectedObservation = min(observations.size - 1, selectedObservation + 1)
    )

    fun selectPrevious(): Trial = copy(
            selectedObservation = max(0, selectedObservation - 1)
    )

    fun summary(): TrialSummary = TrialSummary(name, state)
}

data class TrialSummary(val name: String, val state: State) {
    companion object {
        val lens: BiDiBodyLens<TrialSummary> = Body.auto<TrialSummary>().toLens()
    }
}

data class Observation(val scaleName: String, val events: List<Event>)
data class Event(val time: Instant, val intensity: Double)

data class UiTrial(val name: String, val observations: List<UiObservation>, val selectedObservation: Int?, val state: State) {
    companion object {
        fun from(trial: Trial, scales: List<Scale>): UiTrial =
                UiTrial(trial.name, trial.observations.map { observation ->
                    UiObservation.from(observation, scales)
                }, trial.selectedObservation, trial.state)
        val lens: BiDiBodyLens<UiTrial> = Body.auto<UiTrial>().toLens()
    }
}
data class UiObservation(val scale: Scale, val selectedIntensity: Int?) {
    companion object {
        fun from(observation: Observation, scales: List<Scale>): UiObservation {
            return scales.find { it.name == observation.scaleName }?.let { scale ->
                val selectedIntensity = if (observation.events.isEmpty()) {
                    null
                } else {
                    val lastEvent = observation.events.last()
                    scale.intensities.indexOfFirst { it.number == lastEvent.intensity }
                }
                UiObservation(scale, selectedIntensity)
            } ?: throw RuntimeException("Failed to find scale")
        }
    }
}