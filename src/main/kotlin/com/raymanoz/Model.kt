package com.raymanoz

import java.time.Instant

data class Intensity(val number: Float, val label: String)
data class Scale(val name: String, val description: String, val intensities: List<Intensity>)
data class Trial(val name: String, val scale: String, val entries: List<Entry>)
data class Entry(val time: Instant?, val intensity: String)
data class NewTrial(val name: String, val scale: String)