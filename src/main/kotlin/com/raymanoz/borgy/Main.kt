package com.raymanoz.borgy

import com.raymanoz.borgy.scale.FileScalesRepository
import org.http4k.cloudnative.env.Environment
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val config = load(Environment.ENV)
    App(config, FileScalesRepository(config.scalesFile)).asServer(SunHttp(config.port)).start()

    println("Borgy (${config.javaClass.simpleName}) is running: http://localhost:${config.port}/")
}
