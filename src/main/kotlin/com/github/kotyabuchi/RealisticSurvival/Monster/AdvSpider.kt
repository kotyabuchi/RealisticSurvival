package com.github.kotyabuchi.RealisticSurvival.Monster

import com.github.kotyabuchi.RealisticSurvival.Monster.Action.FightingAction
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.max
import kotlin.math.min

object AdvSpider: AdvancedMonster(EntityType.SPIDER, EntityType.CAVE_SPIDER) {

    init {
        addFightingAction(FightingAction(main, 10L, .3, 20 * 5) { mob ->
            val target = mob.target ?: return@FightingAction
            val spiderLoc = mob.location.toVector()
            val targetLoc = target.location.add(0.0, 0.5, 0.0).toVector()
            val webLocation = targetLoc.clone().subtract(spiderLoc).normalize().multiply(0.3)
            val web = mob.world.spawnFallingBlock(mob.location.add(webLocation), Material.COBWEB.createBlockData())
            web.velocity = targetLoc.clone().subtract(spiderLoc).multiply(0.2)
        })

        addFightingAction(FightingAction(main, 10L, .5, 20 * 3) { mob ->
            val target = mob.target ?: return@FightingAction
            val targetLoc = target.location.add(.0, target.eyeHeight / 2, .0)
            val spiderLoc = mob.location
            val jumpLoc = targetLoc.add(.0, spiderLoc.distance(targetLoc) / 40.0, .0).toVector()
            val velocity = jumpLoc.clone().subtract(spiderLoc.toVector()).multiply(0.3)
            velocity.x = max(-4.0, min(velocity.x, 4.0))
            velocity.y = max(-4.0, min(velocity.y, 4.0))
            velocity.z = max(-4.0, min(velocity.z, 4.0))
            mob.velocity = velocity
        })
    }

    @EventHandler
    fun onLand(event: EntityChangeBlockEvent) {
        val entity = event.entity as? FallingBlock ?: return
        val material = entity.blockData.material
        if (material == Material.COBWEB) {
            object : BukkitRunnable() {
                override fun run() {
                    if (event.block.type == Material.COBWEB) event.block.type = Material.AIR
                }
            }.runTaskLater(main, 20 * 5)
        }
    }

    @EventHandler
    fun onDrop(event: EntityDropItemEvent) {
        val entity = event.entity as? FallingBlock ?: return
        val material = entity.blockData.material
        if (material == Material.COBWEB) {
            event.isCancelled = true
            entity.remove()
        }
    }
}