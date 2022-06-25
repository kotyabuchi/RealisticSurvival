package com.github.kotyabuchi.RealisticSurvival.Utility

inline fun <reified T : Enum<T>> valueOfOrNull(type: String): T? {
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun <T: Any> List<T?>.replaceNull(obj: T): List<T> {
    val result = mutableListOf<T>()
    this.forEach {
        result.add(it ?: obj)
    }
    return result
}

fun Boolean.toInt(): Int = if (this) 1 else 0

inline fun <T: Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
    if (elements.all { it != null }) {
        closure(elements.filterNotNull())
    }
}