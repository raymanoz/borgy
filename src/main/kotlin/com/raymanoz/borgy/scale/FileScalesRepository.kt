package com.raymanoz.borgy.scale

import com.raymanoz.util.Json.loadStreamOrDie
import org.http4k.format.Jackson
import org.http4k.format.Jackson.asA

class FileScalesRepository(scalesStream: String) : ScalesRepository {
    private val scales: List<Scale> = Jackson.parse(loadStreamOrDie(scalesStream).use { s ->
        s.bufferedReader().use { r ->
            r.readText()
        }
    }).asA<Array<Scale>>().toList()

    override fun scales(): List<Scale> = scales
}