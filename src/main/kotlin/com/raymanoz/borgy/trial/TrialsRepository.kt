package com.raymanoz.borgy.trial

interface TrialsRepository {
    fun names(): List<String>
    fun newTrial(name: String, scales: List<String>): Trial
    fun get(name: String): Trial?
    fun put(trial: Trial): Trial
    fun complete(name: String)
}

