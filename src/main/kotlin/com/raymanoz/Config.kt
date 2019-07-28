package com.raymanoz

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.BiDiLens
import org.http4k.lens.Lens
import org.http4k.lens.int
import org.http4k.lens.string
import org.http4k.routing.ResourceLoader
import java.io.File

interface Config {
    val scalesFile: Lens<Environment, String>
    val activeTrials: Lens<Environment, File>
    val completeTrials: Lens<Environment, File>
    val port: Lens<Environment, Int>
    val resourceLoader: ResourceLoader
}

object JarConfig : Config {
    override val scalesFile = EnvironmentKey.string().defaulted("scales.file", "/scales.json")
    override val activeTrials = EnvironmentKey.string().map(::File).defaulted("trials.active", File("trials/active"))
    override val completeTrials = EnvironmentKey.string().map(::File).defaulted("trials.complete", File("trials/complete"))
    override val port = EnvironmentKey.int().defaulted("port", 9000)
    override val resourceLoader = ResourceLoader.Classpath("/client")
}

object LocalDevConfig : Config {
    override val scalesFile = EnvironmentKey.string().defaulted("scales.file", "/scales.json")
    override val activeTrials = EnvironmentKey.string().map(::File).defaulted("trials.active", File("trials/active"))
    override val completeTrials = EnvironmentKey.string().map(::File).defaulted("trials.complete", File("trials/complete"))
    override val port = EnvironmentKey.int().defaulted("port", 9000)
    override val resourceLoader = ResourceLoader.Directory("src/main/react/build")
}