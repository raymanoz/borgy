package com.raymanoz.borgy.scale

data class Scale(val name: String, val description: String, val intensities: List<Intensity>)
data class Intensity(val number: Double, val label: String)
