package com.github.kotyabuchi.RealisticSurvival.Monster.Action

import org.bukkit.entity.Mob

open class MobAction(private val chance: Double = 1.0, private val action: (entity: Mob) -> Unit) {

    open fun canAction(entity: Mob): Boolean {
        return Math.random() < chance
    }

    open fun doAction(entity: Mob) {
        action(entity)
    }
}