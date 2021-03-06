package com.raymanoz.borgy.trial

import org.http4k.format.Jackson
import org.http4k.format.Jackson.asA
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FileTrialsRepository(val trialsDirectory: File, val archivedTrialsDirectory: File) : TrialsRepository {
    override fun get(): List<Trial> = (activeTrialsDir()
            .listFiles { _, name -> name.endsWith("json") } ?: arrayOf())
            .map { it.read<Trial>() }

    override fun newTrial(name: String, scales: List<String>): Trial {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"))
        val timestampedName = "${name}_${timestamp}"
        val trial = Trial(timestampedName,
                scales.map{ scale -> Observation(scale, emptyList())}
        )

        trialFile(timestampedName).apply {
            createNewFile()
            write(trial)
        }

        return trial
    }

    override fun get(name: String): Trial? =
            withTrialFile(name) { it.read() }

    override fun complete(name: String) {
        withTrialFile(name) {
            completeTrials()
            Files.move(trialFile(name).toPath(), completeFile(name).toPath())
        }
    }

    override fun put(trial: Trial): Trial =
            withTrialFile(trial.name) {
                it.write(trial)
                trial
            }

    private fun trialFile(name: String): File = File(activeTrialsDir(), "$name.json")

    private fun activeTrialsDir() = this.trialsDirectory.apply { mkdirs() }

    private fun completeFile(name: String): File = File(completeTrials(), "$name.json")

    private fun completeTrials() = this.archivedTrialsDirectory.apply { mkdirs() }

    private fun File.load(): File? = if (exists()) this else null

    private inline fun <reified T : Any> File.write(data: T) = writeText(Jackson.asJsonString(data))

    private inline fun <reified T : Any> File.read(): T = readText().asA(T::class)

    private fun <T> withTrialFile(name: String, trialFn: (File) -> T): T {
        return trialFile(name).load()?.let { trialFn(it) } ?: throw Error("$name not found")
    }

}