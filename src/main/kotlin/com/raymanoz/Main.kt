package com.raymanoz

import org.http4k.cloudnative.env.Environment
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val env = Environment.ENV
    val config = LocalDevConfig
    BorgyApp(config, env).asServer(SunHttp(config.port(env))).start()
}
