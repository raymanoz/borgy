package com.raymanoz.util

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success

fun <N, F> N?.nullToResult(nullReason: F): Result<N, F> =
        this?.let { Success(it) } ?: Failure(nullReason)
