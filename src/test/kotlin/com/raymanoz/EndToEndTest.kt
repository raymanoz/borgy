package com.raymanoz

import com.natpryce.hamkrest.assertion.assertThat
import com.raymanoz.scales.InMemoryScalesRepository
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
    private val server = BorgyApp(config, InMemoryScalesRepository()).asServer(SunHttp(config.port))

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
