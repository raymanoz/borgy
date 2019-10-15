package com.raymanoz.borgy

import com.raymanoz.borgy.scale.ScalesRepository
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
import org.http4k.core.with
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.asA
import org.http4k.format.Jackson.asJsonString
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*

class App(private val config: Config, private val scales: ScalesRepository) : HttpHandler {

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
                                    DELETE to endTrial(),
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

    private fun endTrial(): HttpHandler = { req ->
        completeTrials()
        val name = req.path("name")
        Files.move(trialFile(name).toPath(), completeFile(name).toPath())
        Response(OK)
    }

    private fun trials(): HttpHandler = { _ ->
        val trials = loadTrials().map { t -> t.name }
        val lens = Body.auto<List<String>>().toLens()
        Response(OK).with(lens of trials)
    }

    private fun trial(): HttpHandler = { req: Request ->
        val lens = Body.auto<Trial>().toLens()
        val data = trialFile(req.path("name")).load()
                .map { it.read<Trial>() }
                .orElseGet { Trial("", "", emptyList()) } // TODO: handle this failure case
        Response(OK).with(lens of data)
    }

    private fun loadTrials(): List<Trial> = (activeTrials()
            .listFiles { _, name -> name.endsWith("json") } ?: arrayOf<File>())
            .map { it.read<Trial>() }

    private fun trialFile(name: String?): File = File(activeTrials(), "$name.json")
    private fun completeFile(name: String?): File = File(completeTrials(), "$name.json")

    private fun newTrial(): HttpHandler = { req ->
        val timestamp = now().format(ofPattern("yyyyMMddhhmmss"))
        val lens = Body.auto<NewTrial>().toLens()
        val newTrial = lens(req)
        val name = "${newTrial.name}_$timestamp"

        // TODO: Handle failure
        activeTrials().mkdirs()
        trialFile(name).apply {
            createNewFile()
            write(Trial(name, newTrial.scale, emptyList()))
        }

        Response(OK).body("""{ "url" : "/trial/$name" } """)
    }

    private fun addTrialButtonClick(): HttpHandler = { req ->
        val elementLens = Body.auto<Entry>().toLens()
        val entry = elementLens(req).copy(time = Instant.now())

        val trialFile = trialFile(req.path("name"))

        val data = trialFile.load()
                .map { it.read<Trial>() }
                .orElseGet { Trial("name", "scale", emptyList()) }

        val newData = data.copy(entries = data.entries + entry)

        trialFile.write(newData)

        Response(OK)
    }

    private fun scale(): HttpHandler = { req ->
        req.path("name")?.let { name ->
            scales.scale(name)?.let { scale ->
                Response(OK).body(asJsonString(scale))
            } ?: Response(NOT_FOUND).body("No scale found for '$name'")
        } ?: Response(BAD_REQUEST).body("Path parameter 'name' missing")
    }

    private fun scales(): HttpHandler = { _ -> Response(OK).body(asJsonString(scales.scales())) }

    private fun File.load(): Optional<File> = if (exists()) Optional.of(this) else Optional.empty()

    private inline fun <reified T : Any> File.write(data: T) = writeText(asJsonString(data))

    private fun activeTrials() = config.activeTrials.apply { mkdirs() }

    private fun completeTrials() = config.completeTrials.apply { mkdirs() }

    private inline fun <reified T : Any> File.read(): T = readText().asA(T::class)
}
