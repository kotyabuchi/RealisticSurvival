package com.github.kotyabuchi.RealisticSurvival.Monster.Action

import com.github.kotyabuchi.RealisticSurvival.Main
import org.bukkit.entity.Mob
import org.bukkit.scheduler.BukkitRunnable

data class FightingAction(private val main: Main, val checkFrequency: Long, private val chance: Double = 1.0, private val coolTime: Long? = null, private val action: (entity: Mob) -> Unit): MobAction(chance, action) {

    private val coolDownList = mutableSetOf<Mob>()

    override fun canAction(entity: Mob): Boolean {
        if (coolDownList.contains(entity)) return false
        return super.canAction(entity)
    }

    override fun doAction(entity: Mob) {
        coolTime?.let {
            coolDownList.add(entity)
            object : BukkitRunnable() {
                override fun run() {
                    coolDownList.remove(entity)
                }
            }.runTaskLater(main, it)
        }
        super.doAction(entity)
    }
}