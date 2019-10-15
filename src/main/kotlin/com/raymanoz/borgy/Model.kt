package com.raymanoz.borgy

import java.time.Instant

data class Intensity(val number: Double, val label: String)
data class Trial(val name: String, val scale: String, val entries: List<Entry>)
data class Entry(val time: Instant?, val intensity: String)
data class NewTrial(val name: String, val scale: String)