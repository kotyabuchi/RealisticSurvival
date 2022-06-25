package com.github.kotyabuchi.RealisticSurvival.Monster

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Monster.Action.FightingAction
import com.github.kotyabuchi.RealisticSurvival.Monster.Action.MobAction
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class AdvancedMonster(protected vararg val entityTypes: EntityType): Listener, KoinComponent {

    protected val main: Main by inject()

    private val spawnActions = mutableSetOf<MobAction>()
    private val startFightActions = mutableSetOf<MobAction>()
    private val fightingEntities = mutableSetOf<Mob>()
    private val finishedFightActions = mutableSetOf<MobAction>()
    private val deathActions = mutableSetOf<MobAction>()
    private var hasFightingAction = false

    @EventHandler
    fun onSpawnEvent(event: EntitySpawnEvent) {
        if (!entityTypes.contains(event.entityType)) return
        val entity = event.entity as? Mob ?: return
        spawnActions.forEach { action ->
            if (action.canAction(entity)) action.doAction(entity)
        }
    }

    @EventHandler
    fun onTargetEvent(event: EntityTargetLivingEntityEvent) {
        if (!entityTypes.contains(event.entityType)) return
        val entity = event.entity as? Mob ?: return
        startFightActions.forEach { action ->
            if (action.canAction(entity)) action.doAction(entity)
        }
        fightingEntities.add(entity)
    }

    @EventHandler
    fun onDeathEvent(event: EntityDeathEvent) {
        if (!entityTypes.contains(event.entityType)) return
        val entity = event.entity as? Mob ?: return
        deathActions.forEach { action ->
            if (action.canAction(entity)) action.doAction(entity)
        }
    }

    protected fun addSpawnAction(action: MobAction) {
        spawnActions.add(action)
    }

    protected fun addStartFightAction(action: MobAction) {
        startFightActions.add(action)
    }

    protected fun addFightingAction(action: FightingAction) {
        if (!hasFightingAction) {
            object : BukkitRunnable() {
                override fun run() {
                    val removeList = mutableSetOf<Mob>()
                    fightingEntities.forEach { mob ->
                        if (mob.target == null) {
                            removeList.add(mob)
                            finishedFightActions.forEach { action ->
                                if (action.canAction(mob)) action.doAction(mob)
                            }
                        }
                    }
                    fightingEntities.removeAll(removeList)
                }
            }.runTaskTimer(main, 0, 5)
        }
        object : BukkitRunnable() {
            override fun run() {
                val removeList = mutableListOf<Entity>()
                for (entity in fightingEntities) {
                    if (entity.isValid) {
                        if (action.canAction(entity)) action.doAction(entity)
                    } else {
                        removeList.add(entity)
                    }
                }
                fightingEntities.removeAll(removeList.toSet())
            }
        }.runTaskTimer(main, 0, action.checkFrequency)
    }

    protected fun addFinishedFightAction(action: MobAction) {
        finishedFightActions.add(action)
    }

    protected fun addDeathAction(action: MobAction) {
        deathActions.add(action)
    }
}