package com.raymanoz

import com.raymanoz.borgy.Config
import org.http4k.routing.ResourceLoader
import java.io.File

class StubConfig : Config {
    override val scalesFile: String = ""
    override val trialsDirectory: File = File("active")
    override val completeTrialsDirectory: File = File("complete")
    override val port: Int = 0
    override val client: ResourceLoader = ResourceLoader.Directory("client")
}