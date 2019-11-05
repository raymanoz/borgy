package com.raymanoz

import com.natpryce.hamkrest.assertion.assertThat
import com.raymanoz.borgy.App
import com.raymanoz.borgy.scale.InMemoryScalesRepository
import com.raymanoz.borgy.scale.ScalesEndpoints
import com.raymanoz.borgy.trial.InMemoryTrialsRepository
import com.raymanoz.borgy.trial.TrialsEndpoints
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasStatus
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EndToEndTest {
    private val client = OkHttp()
    private val config = StubConfig()
    private val trialsRepository = InMemoryTrialsRepository()
    private val scalesRepository = InMemoryScalesRepository()
    private val server = App(config, TrialsEndpoints(trialsRepository, emptyList()), ScalesEndpoints(scalesRepository)).asServer(SunHttp(config.port))

    @BeforeEach
    fun setup() {
        server.start()
    }

    @AfterEach
    fun teardown() {
        server.stop()
    }

    @Test
    fun `all endpoints are mounted correctly`() {
        assertThat(client(Request(Method.GET, "http://localhost:${server.port()}/api/ping")), hasStatus(OK))
        assertThat(client(Request(Method.GET, "http://localhost:${server.port()}/api/scales")), hasStatus(OK))
        assertThat(client(Request(Method.GET, "http://localhost:${server.port()}/api/scales/scale1")), hasStatus(OK))
    }
}
