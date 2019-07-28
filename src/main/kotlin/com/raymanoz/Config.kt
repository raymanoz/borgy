package com.raymanoz

import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.int
import org.http4k.lens.string
import org.http4k.routing.ResourceLoader
import java.io.File

object Config {
    val scalesFile = EnvironmentKey.string().defaulted("scales.file", "/scales.json")
    val activeTrials = EnvironmentKey.string().map(::File).defaulted("trials.active", File("trials/active"))
    val completeTrials = EnvironmentKey.string().map(::File).defaulted("trials.complete", File("trials/complete"))
    val port = EnvironmentKey.int().defaulted("port", 9000)
//    val resourceLoader = ResourceLoader.Classpath("/client")
    val resourceLoader = ResourceLoader.Directory("src/main/react/build")
}