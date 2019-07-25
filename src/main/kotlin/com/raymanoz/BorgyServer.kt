package com.raymanoz

import com.google.gson.Gson
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PATCH
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.ServerFilters
import org.http4k.format.Gson.auto
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.time.Instant
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Optional

class BorgyServer(private val gson: Gson, private val config: Configuration) {
    fun start() {
        val httpHandler = routes(
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
            static(config.client())
        )

        ServerFilters.Cors(UnsafeGlobalPermissive).then(httpHandler).asServer(Jetty(9000)).start()
    }

    private fun ping(): HttpHandler = { Response(OK).body("pong") }

    private fun endTrial(): HttpHandler = { req ->
        completeTrials().mkdirs()
        val name = req.path("name")
        Files.move(trialFile(name).toPath(), completeFile(name).toPath())
        Response(OK)
    }

    private fun trials(): HttpHandler = { _ ->
        val trials = loadTrials().map { t -> t.name }
        val trialsList = gson.toJson(trials)
        Response(OK).body(trialsList)
    }

    private fun trial(): HttpHandler = { req: Request ->
        val data = load(trialFile(req.path("name")))
            .map { f -> InputStreamReader(FileInputStream(f)) }
            .map { jsonString -> gson.fromJson(jsonString, Trial::class.java) }
            .orElseGet { Trial("", "", emptyList()) } // TODO: handle this failure case
        Response(OK).body(gson.toJson(data))
    }

    private fun loadTrials(): List<Trial> {
        activeTrials().mkdirs()
        return (activeTrials()
            .listFiles { _, name -> name.endsWith("json") } ?: arrayOf<File>())
            .map { f -> InputStreamReader(FileInputStream(f)) }
            .map { jsonString -> gson.fromJson(jsonString, Trial::class.java) }
    }

    private fun trialFile(name: String?): File = File(activeTrials(), "$name.json")
    private fun completeFile(name: String?): File = File(completeTrials(), "$name.json")

    private fun newTrial(): HttpHandler = { req ->
        val timestamp = now().format(ofPattern("yyyyMMddhhmmss"))
        val newTrial = gson.fromJson(req.bodyString(), NewTrial::class.java)
        val name = "${newTrial.name}_$timestamp"

        // TODO: Handle failure
        activeTrials().mkdirs()
        val trialFile = trialFile(name)
        trialFile.createNewFile()

        write(Trial(name, newTrial.scale, emptyList()), trialFile)

        Response(OK).body("""{ "url" : "/trial/$name" } """)
    }

    private fun addTrialButtonClick(): HttpHandler = { req ->
        val elementLens = Body.auto<Entry>().toLens()
        val entry = elementLens(req).copy(time = Instant.now())

        val trialFile = trialFile(req.path("name"))

        val data = load(trialFile)
            .map { f -> InputStreamReader(FileInputStream(f)) }
            .map { jsonString -> gson.fromJson(jsonString, Trial::class.java) }
            .orElseGet { Trial("name", "scale", emptyList()) }

        val newData = data.copy(entries = data.entries + entry)

        write(newData, trialFile)

        Response(OK)
    }

    private fun scale(): HttpHandler = { req ->
        val data = loadStream(config.scalesFile())
            .map { f -> f.bufferedReader().readText() }
            .map { json -> gson.fromJson(json, Array<Scale>::class.java) }
            .orElse(emptyArray()).toList()

        val scale: Scale? = data.find { scale -> scale.name == req.path("name") }

        if (scale == null) {
            Response(Status.NOT_FOUND)
        } else {
            Response(OK).body(gson.toJson(scale))
        }
    }

    private fun scales(): HttpHandler {
        return { _ ->
            val data = loadStream(config.scalesFile())
                .map { f -> f.bufferedReader().readText() }
                .orElse("[]")
            Response(OK).body(data)
        }
    }

    private fun load(file: File): Optional<File> = if (file.exists()) Optional.of(file) else Optional.empty()

    private fun load(filename: String): Optional<File> = load(File(filename))

    private fun loadStream(name: String): Optional<InputStream> =
        Optional.ofNullable((BorgyServer::class.java).getResourceAsStream(name))

    private fun write(data: Trial, file: File) = file.writeText(gson.toJson(data, Trial::class.java))

    private fun activeTrials(): File {
        config.activeTrials().mkdirs()
        return config.activeTrials()
    }

    private fun completeTrials(): File {
        config.completeTrials().mkdirs()
        return config.completeTrials()
    }

}
