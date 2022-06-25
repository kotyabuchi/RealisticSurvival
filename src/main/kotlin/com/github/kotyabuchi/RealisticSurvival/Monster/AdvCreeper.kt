package com.github.kotyabuchi.RealisticSurvival.Monster

import org.bukkit.Material
import org.bukkit.block.BlockState
import org.bukkit.block.Container
import org.bukkit.entity.Creeper
import org.bukkit.entity.EntityType
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.*
import org.bukkit.util.Vector

object AdvCreeper: AdvancedMonster(EntityType.CREEPER) {

    private val blockStateBackup = mutableMapOf<FallingBlock, BlockState>()

    @EventHandler
    fun onDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return
        val entity = event.entity

        if (entity is Creeper) {
            entity.explode()
        } else {
            val damager = event.damager
            val entityLoc = entity.location.toVector()
            val damagerLoc = damager.location.toVector()
            entity.velocity = entityLoc.subtract(damagerLoc).normalize()
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val entity = event.entity as? Creeper ?: return
        if (event.cause == EntityDamageEvent.DamageCause.FIRE || event.cause == EntityDamageEvent.DamageCause.FIRE_TICK) entity.explode()
    }

    @EventHandler
    fun onExplode(event: EntityExplodeEvent) {
        val entity = event.entity
        val blocks = event.blockList().sortedBy { it.y }.reversed()
        val entityLoc = entity.location.toVector()
        for (block in blocks) {
            if (block.state is Container) continue
            val spawnLocation = block.location.toCenterLocation()
            spawnLocation.y = block.location.y
            val fallingBlock = block.world.spawnFallingBlock(spawnLocation, block.blockData)
            fallingBlock.setHurtEntities(true)
            blockStateBackup[fallingBlock] = block.getState(true)
            val vec = spawnLocation.toVector().subtract(entityLoc).normalize().add(Vector(.0, 1.2, .0))
            fallingBlock.velocity = vec
            block.type = Material.AIR
        }
        event.blockList().clear()
    }

    @EventHandler
    fun onPut(event: EntityChangeBlockEvent) {
        val entity = event.entity as? FallingBlock ?: return
        val blockState = blockStateBackup[entity] ?: return
        val targetBlock = blockState.location.block
        if (targetBlock.type != Material.AIR && !targetBlock.isLiquid) return
        blockState.update(true)
        blockStateBackup.remove(entity)
        entity.remove()
        event.isCancelled = true
    }

    @EventHandler
    fun onDrop(event: EntityDropItemEvent) {
        val entity = event.entity as? FallingBlock ?: return
        val blockState = blockStateBackup[entity] ?: return
        val targetBlock = blockState.location.block
        if (targetBlock.type != Material.AIR && !targetBlock.isLiquid) return
        blockState.update(true)
        blockStateBackup.remove(entity)
        entity.remove()
        event.isCancelled = true
    }
}