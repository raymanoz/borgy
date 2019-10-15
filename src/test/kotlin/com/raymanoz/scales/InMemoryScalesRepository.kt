package com.raymanoz.scales

import com.raymanoz.Intensity

class InMemoryScalesRepository(scales: List<Scale>? = null) : ScalesRepository {

    private val internalScales = scales ?: listOf(
            Scale("scale1", "scale used for testing",
                    listOf(
                            Intensity(1.0, "first intensity"),
                            Intensity(2.5, "second intensity"),
                            Intensity(3.0, "third intensity"),
                            Intensity(7.70, "last intensity")

                    )),
            Scale("scale2", "yet another scale",
                    listOf(
                            Intensity(50.0, "fifty"),
                            Intensity(60.0, "sixty")

                    ))
    )

    override fun scales(): List<Scale> = this.internalScales

}