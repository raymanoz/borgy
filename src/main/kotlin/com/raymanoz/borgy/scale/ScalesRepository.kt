package com.raymanoz.borgy.scale

interface ScalesRepository {
    fun scales(): List<Scale>
    fun get(name: String): Scale? = scales().find { s -> s.name == name }
}

