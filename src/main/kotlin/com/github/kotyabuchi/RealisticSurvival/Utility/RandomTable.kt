package com.github.kotyabuchi.RealisticSurvival.Utility

import org.apache.commons.lang.math.DoubleRange

class RandomTable<T> {

    private var usedRange: Double = 0.0
    private val tables: MutableMap<T, DoubleRange> = mutableMapOf()

    fun addItem(item: T, rate: Double): Boolean {
        if (usedRange + rate > 1) return false
        tables[item] = DoubleRange(usedRange, usedRange + rate)
        usedRange += rate
        return true
    }

    fun remove(item: T) {
        tables.remove(item)?.let { range ->
            usedRange -= (range.maximumDouble - range.minimumDouble)
            usedRange = 0.0
            val cache = tables.toMap()
            tables.clear()
            cache.forEach {
                addItem(it.key, (it.value.maximumDouble - it.value.minimumDouble))
            }
        }
    }

    fun getRandom(): T? {
        val random = Math.random()
        for ((any, range) in tables) {
            if (range.containsDouble(random)) return any
        }
        return null
    }
}