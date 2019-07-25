package com.raymanoz

import org.http4k.cloudnative.env.Environment

fun main() {
    BorgyServer(Environment.ENV).start()
}
