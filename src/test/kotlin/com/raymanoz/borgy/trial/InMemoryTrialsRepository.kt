package com.raymanoz.borgy.trial

class InMemoryTrialsRepository() : TrialsRepository {
    override fun put(trial: Trial): Trial {
        TODO("not implemented")
    }

    override fun newTrial(name: String, scales: List<String>): Trial {
        TODO("not implemented")
    }

    override fun get(): List<Trial> {
        TODO("not implemented")
    }

    override fun get(name: String): Trial? {
        TODO("not implemented")
    }

    override fun complete(name: String) {
        TODO("not implemented")
    }
}
