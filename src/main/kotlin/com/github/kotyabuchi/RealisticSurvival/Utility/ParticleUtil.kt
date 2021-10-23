package com.github.kotyabuchi.RealisticSurvival.Utility

import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

object ParticleUtil {

    fun circleLocations(radius: Double, pointAmount: Int = ceil(radius * 18).toInt(), startPoint: Double = 0.0): List<Pair<Double, Double>> {
        val result = mutableListOf<Pair<Double, Double>>()
        val width = 360.0 / pointAmount
        for (i in (0 until pointAmount)) {
            result.add(Pair(radius * cos(Math.toRadians(width * i + startPoint)), radius * -sin(Math.toRadians(width * i + startPoint))))
        }
        return result
    }
}