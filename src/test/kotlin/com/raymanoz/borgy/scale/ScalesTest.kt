package com.raymanoz.borgy.scale

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.raymanoz.StubConfig
import com.raymanoz.borgy.App
import com.raymanoz.borgy.trial.InMemoryTrialsRepository
import com.raymanoz.borgy.trial.TrialsEndpoints
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test

class ScalesTest {
    private val scales = listOf(
            Scale("name1", "desc1", listOf(
                    Intensity(1.0, "intensity1")
            )),
            Scale("name2", "desc1", listOf(
                    Intensity(2.0, "intensity2")
            ))
    )

    private val client = App(StubConfig(), TrialsEndpoints(InMemoryTrialsRepository(), emptyList()), ScalesEndpoints(InMemoryScalesRepository(scales)))

    @Test
    fun `can list all scales`() {
        assertThat(
                client(Request(Method.GET, "/api/scales")),
                Jackson.hasBody(
                        """[
                          |  {"name":"name1","description":"desc1","intensities":[{"number":1,"label":"intensity1"}]},
                          |  {"name":"name2","description":"desc1","intensities":[{"number":2,"label":"intensity2"}]}
                          |]""".trimMargin())
        )
    }

    @Test
    fun `can lookup a single scale`() {
        assertThat(
                client(Request(Method.GET, "/api/scales/name1")),
                hasBody("""{"name":"name1","description":"desc1","intensities":[{"number":1,"label":"intensity1"}]}""")
        )
    }

    @Test
    fun `404 if the scale does not exist`() {
        val response = client(Request(Method.GET, "/api/scales/foo"))
        assertThat(
                response,
                hasStatus(Status.NOT_FOUND).and(hasBody("No scale found for 'foo'"))
        )
    }
}
