package com.raymanoz.borgy

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.int
import org.http4k.lens.string
import org.http4k.routing.ResourceLoader
import java.io.File
import kotlin.system.exitProcess


fun load(env: Environment): Config = when (System.getProperty("config", "jar")) {
    "dev" -> DevConfig(env)
    "jar" -> JarConfig(env)
    else -> {
        System.err.println("Unknown config: " + env["config"])
        exitProcess(-1)
    }
}

interface Config {
    val scalesFile: String
    val trialsDirectory: File
    val completeTrialsDirectory: File
    val port: Int
    val client: ResourceLoader
}

class JarConfig(env: Environment) : Config {
    override val scalesFile = EnvironmentKey.string().defaulted("scales.file", "/scales.json")(env)
    override val trialsDirectory = EnvironmentKey.string().map(::File).defaulted("trials.dir", File("trials"))(env)
    override val completeTrialsDirectory = EnvironmentKey.string().map(::File).defaulted("trials.complete.dir", File("trials/complete"))(env)
    override val port = EnvironmentKey.int().defaulted("port", 9000)(env)
    override val client = ResourceLoader.Classpath("/public")
}

class DevConfig(env: Environment) : Config {
    override val scalesFile = EnvironmentKey.string().defaulted("scales.file", "/scales.json")(env)
    override val trialsDirectory = EnvironmentKey.string().map(::File).defaulted("trials.dir", File("trials"))(env)
    override val completeTrialsDirectory = EnvironmentKey.string().map(::File).defaulted("trials.complete.dir", File("trials/complete"))(env)
    override val port = EnvironmentKey.int().defaulted("port", 9000)(env)
    override val client = ResourceLoader.Directory("src/main/react/build")
}