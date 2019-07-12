package com.raymanoz

import com.google.gson.Gson
import org.http4k.core.*
import org.http4k.filter.CorsPolicy
import org.http4k.filter.ServerFilters
import org.http4k.format.Gson.auto
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.time.Instant
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*

class BorgyServer(private val gson: Gson) {
    private val trialDirectory = File("out", "activeTrials")
    private val completeDirectory = File("out", "completeTrials")

    fun start() {
        val httpHandler = routes(
                "/api/scales" bind Method.GET to scales(),
                "/api/scales/{name}" bind Method.GET to scale(),
                "/api/trials" bind Method.POST to newTrial(),
                "/api/trials/{name}" bind Method.GET to trial(),
                "/api/trials/{name}" bind Method.DELETE to endTrial(),
                "/api/trials" bind Method.GET to trials(),
                "/api/trials/{name}" bind Method.PATCH to addTrialButtonClick()
        )
        ServerFilters.Cors(CorsPolicy.UnsafeGlobalPermissive).then(httpHandler).asServer(Jetty(9000)).start()
    }

    private fun endTrial(): HttpHandler = { req ->
        completeDirectory.mkdirs()
        val name = req.path("name")
        Files.move(trialFile(name).toPath(), completeFile(name).toPath())
        Response(Status.OK).header("Access-Control-Allow-Origin", "*")
    }

    private fun trials(): HttpHandler = { _ ->
        val trials = loadTrials().map { t -> t.name }
        val trialsList = gson.toJson(trials)
        Response(Status.OK).header("Access-Control-Allow-Origin", "*").body(trialsList)
    }

    private fun trial(): HttpHandler = { req: Request ->
        val data = load(trialFile(req.path("name")))
                .map { f -> InputStreamReader(FileInputStream(f)) }
                .map { jsonString -> gson.fromJson(jsonString, Trial::class.java) }
                .orElseGet { Trial("", "", emptyList()) } // TODO: handle this failure case
        Response(Status.OK).header("Access-Control-Allow-Origin", "*").body(gson.toJson(data))
    }

    private fun loadTrials(): List<Trial> = trialDirectory
            .listFiles { _, name -> name.endsWith("json") }
            .map { f -> InputStreamReader(FileInputStream(f)) }
            .map { jsonString -> gson.fromJson(jsonString, Trial::class.java) }

    private fun trialFile(name: String?): File = File(trialDirectory, "${name}.json")
    private fun completeFile(name: String?): File = File(completeDirectory, "${name}.json")

    private fun newTrial(): HttpHandler = { req ->
        val timestamp = now().format(ofPattern("yyyyMMddhhmmss"))
        val newTrial = gson.fromJson(req.bodyString(), NewTrial::class.java)
        val name = "${newTrial.name}_${timestamp}"

        // TODO: Handle failure
        val dir = trialDirectory
        dir.mkdirs()
        val trialFile = trialFile(name)
        trialFile.createNewFile()

        write(Trial(name, newTrial.scale, emptyList()), trialFile)

        Response(Status.OK).header("Access-Control-Allow-Origin", "*").body("""{ "url" : "/trial/${name}" } """)
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

        Response(Status.OK).header("Access-Control-Allow-Origin", "*")
    }

    private fun scale(): HttpHandler = { req ->
        val data = load("scales.json")
                .map { f -> f.readText() }
                .map { json -> gson.fromJson(json, Array<Scale>::class.java) }
                .orElse(emptyArray()).toList()

        val scale: Scale? = data.find { scale -> scale.name == req.path("name") }

        if (scale == null) {
            Response(Status.NOT_FOUND)
        } else {
            Response(Status.OK).body(gson.toJson(scale))
        }.header("Access-Control-Allow-Origin", "*")
    }

    private fun scales(): HttpHandler {
        return { _ ->
            val data = load("scales.json")
                    .map { f -> f.readText() }
                    .orElse("[]")
            Response(Status.OK).header("Access-Control-Allow-Origin", "*").body(data)
        }
    }


    private fun load(file: File): Optional<File> = if (file.exists()) Optional.of(file) else Optional.empty()

    private fun load(filename: String): Optional<File> = load(File(filename))

    private fun write(data: Trial, filename: String) = write(data, File(filename))

    private fun write(data: Trial, file: File) = file.writeText(gson.toJson(data, Trial::class.java))
}
