package com.raymanoz.borgy.trial

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class TrialTest {
    @Test
    fun `select next increments selected observation`() {
        val trial = Trial("foo", listOf(Observation("hotness", emptyList(), null), Observation("coldness", emptyList(), null)), 0)
        assertThat(trial.selectNext().selectedObservation, equalTo(1))
    }

    @Test
    fun `select next does not increment past last observation`() {
        val trial = Trial("foo", listOf(Observation("hotness", emptyList(), null), Observation("coldness", emptyList(), null)), 1)
        assertThat(trial.selectNext().selectedObservation, equalTo(1))
    }

    @Test
    fun `select previous decrements selected observation`() {
        val trial = Trial("foo", listOf(Observation("hotness", emptyList(), null), Observation("coldness", emptyList(), null)), 1)
        assertThat(trial.selectPrevious().selectedObservation, equalTo(0))
    }

    @Test
    fun `select previous does not decrement past first observation`() {
        val trial = Trial("foo", listOf(Observation("hotness", emptyList(), null), Observation("coldness", emptyList(), null)), 0)
        assertThat(trial.selectPrevious().selectedObservation, equalTo(0))
    }
}