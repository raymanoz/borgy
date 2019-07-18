package com.raymanoz

import org.http4k.routing.ResourceLoader
import java.io.File

class Configuration {
    fun scalesFile(): String = System.getProperty("scales.file") ?: "/scales.json"
    fun activeTrials(): File = File(System.getProperty("trials.active") ?: "trials/active")
    fun completeTrials(): File = File(System.getProperty("trials.complete") ?: "trials/complete")
    fun client(): ResourceLoader = ResourceLoader.Classpath("/client")
}