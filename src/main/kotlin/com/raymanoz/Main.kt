package com.raymanoz

import org.http4k.cloudnative.env.Environment
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val env = Environment.ENV
    val config = JarConfig
    BorgyApp(config, env).asServer(SunHttp(config.port(env))).start()

    println("Borgy is running: http://localhost:${config.port(env)}/")
}
