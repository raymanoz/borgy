package com.raymanoz

import com.google.gson.Gson
import org.http4k.core.*
import org.http4k.format.Gson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.time.Instant
import java.util.*

class BorgyServer(private val gson: Gson) {
    fun start() {
        routes(
                "/click" bind Method.POST to writeLog(),
                "/scales" bind Method.GET to getScales()
        ).asServer(Jetty(9000)).start()
    }

    private fun writeLog(): HttpHandler {
        return { req ->
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
    }

    private fun getScales(): HttpHandler {
        return { _ ->
            val data = load("scales.json")
                    .map { f -> f.readText() }
                    .orElse("[]")
            Response(Status.OK).header("Access-Control-Allow-Origin", "*").body(data)
        }
    }

    private fun load(filename: String): Optional<File> {
        val file = File(filename)
        return if (file.exists()) Optional.of(file) else Optional.empty()
    }

    private fun write(data: Scale, filename: String) {
        File(filename).writeText(gson.toJson(data, Scale::class.java))
    }

    private fun write(data: BorgData, filename: String) {
        File(filename).writeText(gson.toJson(data, BorgData::class.java))
    }
}
