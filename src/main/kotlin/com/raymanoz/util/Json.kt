package com.raymanoz.util

import org.http4k.format.Jackson
import org.http4k.format.Jackson.asA
import java.io.InputStream

object Json {
    fun <T>parseJsonStream(stream: String): List<T> =
            Jackson.parse(loadStreamOrDie(stream).use { s ->
                s.bufferedReader().use { r ->
                    r.readText()
                }
            }).asA<Array<T>>().toList()

    private fun loadStreamOrDie(name: String): InputStream =
            (Json::class.java).getResourceAsStream(name) ?: throw Error("${name} not found")
}