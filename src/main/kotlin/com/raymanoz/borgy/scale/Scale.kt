package com.raymanoz.borgy.scale

import org.http4k.core.Body
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens

data class Scale(val name: String, val description: String, val intensities: List<Intensity>) {
    companion object {
        val lens: BiDiBodyLens<Scale> = Body.auto<Scale>().toLens()
        val scalesLens: BiDiBodyLens<List<Scale>> = Body.auto<List<Scale>>().toLens()
    }
}
data class Intensity(val number: Double, val label: String)
