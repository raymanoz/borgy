package com.raymanoz

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.Instant

fun main() {
    class UtcDateTypeAdapter : TypeAdapter<Instant>() {
        override fun write(out: JsonWriter, value: Instant) {
            out.value(value.toString())
        }

        override fun read(json: JsonReader): Instant? {
            if (json.peek() == JsonToken.NULL) {
                json.nextNull()
                return null
            }

            return Instant.parse(json.nextString())
        }
    }

    val gson = GsonBuilder()
            .registerTypeAdapter(Instant::class.java, UtcDateTypeAdapter())
            .setPrettyPrinting()
            .create()

    BorgyServer(gson, Configuration()).start()
}
