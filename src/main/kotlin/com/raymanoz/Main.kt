package com.raymanoz

import org.http4k.cloudnative.env.Environment
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val config = Config.load(Environment.ENV)
    BorgyApp(config).asServer(SunHttp(config.port)).start()

    println("Borgy (${config.javaClass.simpleName}) is running: http://localhost:${config.port}/")
}
