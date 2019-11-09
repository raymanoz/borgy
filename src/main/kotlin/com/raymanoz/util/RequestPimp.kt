package com.raymanoz.util

import com.natpryce.Result
import com.natpryce.asResultOr
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path

fun Request.pathResult(name: String): Result<String, Response> =
        this.path(name).asResultOr { Response(Status.BAD_REQUEST).body("Path parameter '$name' missing") }