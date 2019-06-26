package com.raymanoz

import java.time.Instant

data class Intensity(val number: Int, val label: String)
data class Scale(val name: String, val description: String, val intensities: List<Intensity>)
data class BorgData(val scale: String, val entries: List<Entry>)
data class Entry(val time: Instant, val intensity: String)