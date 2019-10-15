package com.raymanoz.borgy.trial

interface TrialsRepository {
    fun names(): List<String>
    fun newTrial(name: String, scale: String): Trial
    fun get(name: String): Trial?
    fun complete(name: String)
    fun put(name: String, entry: Entry): Trial?
}

