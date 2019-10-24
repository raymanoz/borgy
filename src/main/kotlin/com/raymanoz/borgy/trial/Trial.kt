package com.raymanoz.borgy.trial

import java.time.Instant


data class Trial(val name: String, val entries: Map<String, List<Entry>>)
data class Entry(val time: Instant, val intensity: String)

data class State(val scale: String, val intensity: String)