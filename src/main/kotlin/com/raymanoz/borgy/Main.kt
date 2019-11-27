package com.raymanoz.borgy

import com.raymanoz.borgy.scale.FileScalesRepository
import com.raymanoz.borgy.scale.ScalesEndpoints
import com.raymanoz.borgy.trial.FileTrialsRepository
import com.raymanoz.borgy.trial.TrialsEndpoints
import org.http4k.cloudnative.env.Environment
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val config = load(Environment.ENV)
    val trialsRepository = FileTrialsRepository(config.trialsDirectory, config.completeTrialsDirectory)
    val scalesRepository = FileScalesRepository(config.scalesFile)
    val scales = scalesRepository.scales()
    App(config, TrialsEndpoints(trialsRepository, scales), ScalesEndpoints(scalesRepository)).asServer(SunHttp(config.port)).start()

    println("Borgy (${config.javaClass.simpleName}) is running: http://localhost:${config.port}/")
}
