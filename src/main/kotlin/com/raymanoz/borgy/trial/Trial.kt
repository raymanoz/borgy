package com.raymanoz.borgy.trial

import java.time.Instant

data class Trial(val name: String, val scale: String, val entries: List<Entry>)
data class Entry(val time: Instant?, val intensity: String)
