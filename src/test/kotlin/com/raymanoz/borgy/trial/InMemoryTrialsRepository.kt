package com.raymanoz.borgy.trial

class InMemoryTrialsRepository(trials: List<Trial>? = null) : TrialsRepository {
    override fun newTrial(name: String, scales: List<String>): Trial {
        TODO("not implemented")
    }

    override fun put(name: String, state: State): Trial? {
        TODO("not implemented")
    }

    override fun names(): List<String> {
        TODO("not implemented")
    }

    override fun get(name: String): Trial? {
        TODO("not implemented")
    }

    override fun complete(name: String) {
        TODO("not implemented")
    }
}