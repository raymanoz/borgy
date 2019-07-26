package com.raymanoz

import org.http4k.cloudnative.env.Environment
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val env = Environment.ENV
    BorgyApp(env).asServer(Jetty(Config.port(env))).start()
}
