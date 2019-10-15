package com.raymanoz.borgy.trial

class InMemoryTrialsRepository(trials: List<Trial>? = null) : TrialsRepository {
    override fun names(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun newTrial(name: String, scale: String): Trial {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(name: String): Trial? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun complete(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun put(name: String, entry: Entry): Trial? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
