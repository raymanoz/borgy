package com.raymanoz.borgy.scale

import com.raymanoz.util.Json

class FileScalesRepository(scalesStream: String) : ScalesRepository {
    private val scales: List<Scale> = Json.parseJsonStream(scalesStream)

    override fun scales(): List<Scale> = scales
}