package com.raymanoz.borgy.scale

import com.raymanoz.borgy.Intensity
import com.raymanoz.util.Json

data class Scale(val name: String, val description: String, val intensities: List<Intensity>)

interface ScalesRepository {
    fun scales(): List<Scale>
    fun scale(name: String): Scale? = scales().find { s -> s.name == name }
}

class FileScalesRepository(scalesStream: String) : ScalesRepository {
    private val scales: List<Scale> = Json.parseJsonStream(scalesStream)

    override fun scales(): List<Scale> = scales
}