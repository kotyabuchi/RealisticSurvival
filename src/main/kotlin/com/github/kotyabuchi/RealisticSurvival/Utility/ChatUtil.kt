package com.github.kotyabuchi.RealisticSurvival.Utility

import com.github.kotyabuchi.RealisticSurvival.Utility.Enum.Symbol
import org.bukkit.ChatColor
import kotlin.math.floor

fun String.colorS(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}

fun String.upperCamelCase(): String {
    return when (this.length) {
        0 -> ""
        1 -> this.uppercase()
        else -> {
            var result = ""
            this.split("_").forEach {
                result += it[0].uppercase() + it.substring(1).uppercase() + " "
            }
            result.trim()
        }
    }
}

fun Double.floor1Digits(): Double {
    return floor(this * 10.0) / 10.0
}

fun Double.floor2Digits(): Double {
    return floor(this * 100.0) / 100.0
}

fun Double.floor3Digits(): Double {
    return floor(this * 1000.0) / 1000.0
}

fun Int.toRomanNumeral(): String = Symbol.closestBelow(this)
    .let { symbol ->
        if (symbol != null) {
            "$symbol${(this - symbol.decimalValue).toRomanNumeral()}"
        } else {
            ""
        }
    }

fun String.toDecimal() : Int {
    return Symbol.highestStartingSymbol(this)
        .let{ symbol ->
            if (symbol != null) {
                symbol.decimalValue + this.drop(symbol.name.length).toDecimal()
            } else {
                0
            }
        }
}