package com.raymanoz.util

import java.io.InputStream

object Json {
    fun loadStreamOrDie(name: String): InputStream =
            (Json::class.java).getResourceAsStream(name) ?: throw Error("$name not found")
}