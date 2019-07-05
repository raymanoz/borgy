package com.raymanoz

import com.google.gson.Gson
import org.http4k.core.*
import org.http4k.format.Gson.auto
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.time.Instant
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*

class BorgyServer(private val gson: Gson) {
    private val trialDirectory = File("out", "activeTrial")

    fun start() {
        routes(
                "/click" bind Method.POST to writeLog(),
                "/scales" bind Method.GET to getScales(),
                "/trial" bind Method.POST to newTrial(),
                "/trial_/{name}" bind Method.GET to trial()
        ).asServer(Jetty(9000)).start()
    }

    private fun trial(): HttpHandler = { req: Request ->
        val data = load(trialFile(req.path("name")))
                .map { f -> InputStreamReader(FileInputStream(f)) }
                .map { jsonString -> gson.fromJson(jsonString, BorgData::class.java) }
                .orElseGet { BorgData("", emptyList()) } // TODO: handle this failure case
        Response(Status.OK).header("Access-Control-Allow-Origin", "*").body("""{"scale": ${data.scale}}""")

    }

    private fun trialFile(name: String?): File = File(trialDirectory, "${name}.json")

    private fun newTrial(): HttpHandler = { req ->
        val timestamp = now().format(ofPattern("yyyyMMddhhmmss"))
        val newTrial = gson.fromJson(req.bodyString(), NewTrial::class.java)
        val name = "${newTrial.name}_${timestamp}"

        // TODO: Handle failure
        val dir = trialDirectory
        dir.mkdirs()
        val trialFile = trialFile(name)
        trialFile.createNewFile()

        write(BorgData(newTrial.scale, emptyList()), trialFile)

        Response(Status.OK).header("Access-Control-Allow-Origin", "*").body("""{ "url" : "/trial/${name}" } """)
    }

    private fun writeLog(): HttpHandler = { req ->
        val elementLens = Body.auto<Entry>().toLens()
        val entry = elementLens(req).copy(time = Instant.now())

        val data = load("log.json")
                .map { f -> InputStreamReader(FileInputStream(f)) }
                .map { jsonString -> gson.fromJson(jsonString, BorgData::class.java) }
                .orElseGet { BorgData("Fnoobar", emptyList()) }

        val newData = data.copy(entries = data.entries + entry)

        write(newData, "log.json")

        Response(Status.OK).header("Access-Control-Allow-Origin", "*")
    }

    private fun getScales(): HttpHandler {
        return { _ ->
            val data = load("scales.json")
                    .map { f -> f.readText() }
                    .orElse("[]")
            Response(Status.OK).header("Access-Control-Allow-Origin", "*").body(data)
        }
    }


    private fun load(file: File): Optional<File> = if (file.exists()) Optional.of(file) else Optional.empty()

    private fun load(filename: String): Optional<File> = load(File(filename))

    private fun write(data: BorgData, filename: String) = write(data, File(filename))

    private fun write(data: BorgData, file: File) = file.writeText(gson.toJson(data, BorgData::class.java))
}
