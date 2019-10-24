package com.raymanoz.borgy.trial

import org.http4k.format.Jackson
import org.http4k.format.Jackson.asA
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FileTrialsRepository(val activeTrials: File, val completeTrials: File) : TrialsRepository {
    override fun names(): List<String> = loadTrials().map { it.name }

    override fun newTrial(name: String, scales: List<String>): Trial {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"))
        val timestampedName = "${name}_${timestamp}"
        val trial = Trial(timestampedName, scales.associateWith { scale -> emptyList<Entry>() })

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

    override fun put(name: String, state: State): Trial? =
            withTrialFile(name) {
                val data = it.read<Trial>()
                data.entries[state.scale]?.let { entries ->
                    val newData = data.copy(entries = (data.entries + Pair(state.scale, entries + Entry(Instant.now(), state.intensity))))
                    it.write(newData)
                    newData
                }
            }

    private fun trialFile(name: String): File = File(activeTrials(), "$name.json")

    private fun loadTrials(): List<Trial> = (activeTrials()
            .listFiles { _, name -> name.endsWith("json") } ?: arrayOf<File>())
            .map { it.read<Trial>() }

    private fun activeTrials() = this.activeTrials.apply { mkdirs() }

    private fun completeFile(name: String): File = File(completeTrials(), "$name.json")

    private fun completeTrials() = this.completeTrials.apply { mkdirs() }

    private fun File.load(): File? = if (exists()) this else null

    private inline fun <reified T : Any> File.write(data: T) = writeText(Jackson.asJsonString(data))

    private inline fun <reified T : Any> File.read(): T = readText().asA(T::class)

    private fun <T> withTrialFile(name: String, trialFn: (File) -> T): T {
        return trialFile(name).load()?.let { trialFn(it) } ?: throw Error("$name not found")
    }

}