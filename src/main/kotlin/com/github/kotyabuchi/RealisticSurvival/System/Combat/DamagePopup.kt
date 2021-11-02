package com.github.kotyabuchi.RealisticSurvival.System.Combat

import com.github.kotyabuchi.RealisticSurvival.Utility.floor2Digits
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import kotlin.math.ceil
import kotlin.random.Random

object DamagePopup: Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamage(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (event.damage <= 0.01) return
        val entity = event.entity as? LivingEntity ?: return
        popup(entity, event.finalDamage.floor2Digits(), true)
    }

    @EventHandler
    fun onRegen(event: EntityRegainHealthEvent) {
        if (event.isCancelled) return
        if (event.amount <= 0.01) return
        val entity = event.entity as? LivingEntity ?: return
        popup(entity, event.amount.floor2Digits(), false)
    }

    private fun popup(entity: LivingEntity, amount: Double, isDamage: Boolean) {
        if (entity !is Player) {
            if (entity.world.getNearbyEntitiesByType(Player::class.java, entity.location, 30.0).isEmpty()) return
        }
        val eyeHeight = entity.eyeHeight
        val baseLoc = entity.location.add(.0, eyeHeight, .0)
        val x = Random.nextInt(15) / 10.0 - .75
        val y = Random.nextInt(ceil((eyeHeight / 4.0) * 10).toInt()) / 10.0
        val z = Random.nextInt(15) / 10.0 - .75
        val popupLoc = baseLoc.add(x, y, z)
        val color = if (isDamage) NamedTextColor.RED else NamedTextColor.GREEN
        entity.world.spawn(popupLoc, AreaEffectCloud::class.java) {
            it.duration = 30
            it.durationOnUse = 0
            it.radius = 0f
            it.radiusOnUse = 0f
            it.radiusPerTick = 0f
            it.setParticle(Particle.BLOCK_CRACK, Material.AIR.createBlockData())
            it.isCustomNameVisible = true
            it.customName(Component.text(amount, color))
        }
    }
}