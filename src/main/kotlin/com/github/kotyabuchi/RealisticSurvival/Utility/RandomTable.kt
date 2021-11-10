package com.github.kotyabuchi.RealisticSurvival.Utility

import org.apache.commons.lang.math.DoubleRange

class RandomTable<T> {

    private val tables: MutableMap<T, DoubleRange> = mutableMapOf()
    private val weightMap: MutableMap<T, Int> = mutableMapOf()

    fun addItem(item: T, weight: Int): RandomTable<T> {
        weightMap[item] = weight
        return this
    }

    fun generate(): RandomTable<T> {
        val totalWeight = weightMap.values.sum()
        val percentPerWeight = 1.0 / totalWeight
        tables.clear()
        var filledWeight = 0.0
        weightMap.forEach { (item, weight) ->
            val itemWeight = weight * percentPerWeight
            tables[item] = DoubleRange(filledWeight, itemWeight)
            filledWeight += itemWeight
        }
        return this
    }

    fun getRandom(): T? {
        val random = Math.random()
        for ((item, range) in tables) {
            if (range.containsDouble(random)) return item
        }
        return null
    }
}