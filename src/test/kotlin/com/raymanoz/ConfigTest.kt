package com.raymanoz

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.raymanoz.borgy.load
import org.http4k.cloudnative.env.Environment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigTest {
    @Test
    fun canLoadDevConfig() {
        System.setProperty("config", "dev")
        assertThat(load(Environment.ENV).javaClass.simpleName, equalTo("DevConfig"))
    }

    @Test
    fun canLoadJarConfig() {
        System.setProperty("config", "jar")
        assertThat(load(Environment.ENV).javaClass.simpleName, equalTo("JarConfig"))
    }

    @Test
    fun defaultsToJarConfig() {
        assertThat(load(Environment.ENV).javaClass.simpleName, equalTo("JarConfig"))
    }
}