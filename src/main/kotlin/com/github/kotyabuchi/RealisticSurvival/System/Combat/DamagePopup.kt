package com.github.kotyabuchi.RealisticSurvival.System.Combat

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.floor1Digits
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.ceil
import kotlin.random.Random

object DamagePopup: Listener, KoinComponent {

    private val main: Main by inject()
    private val popupStands = mutableListOf<ArmorStand>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamage(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (event.damage <= 0.0) return
        val entity = event.entity as? LivingEntity ?: return
        popup(entity, event.finalDamage.floor1Digits(), true)
    }

    @EventHandler
    fun onRegen(event: EntityRegainHealthEvent) {
        if (event.isCancelled) return
        if (event.amount <= 0.0) return
        val entity = event.entity as? LivingEntity ?: return
        popup(entity, event.amount.floor1Digits(), false)
    }

    private fun popup(entity: LivingEntity, amount: Double, isDamage: Boolean) {
        if (entity !is Player) {
            var needPopup = false
            val nearEntities = entity.getNearbyEntities(30.0, 30.0, 30.0)
            for (i in 0 until nearEntities.size) {
                val checkTarget = nearEntities[i]
                if (checkTarget is Player) {
                    needPopup = true
                    break
                }
            }
            if (!needPopup) return
        }
        val eyeHeight = entity.eyeHeight
        val baseLoc = entity.location.add(.0, eyeHeight, .0)
        val x = Random.nextInt(15) / 10.0 - .75
        val y = Random.nextInt(ceil((eyeHeight / 4.0) * 10).toInt()) / 10.0
        val z = Random.nextInt(15) / 10.0 - .75
        val popupLoc = baseLoc.add(x, y, z)
        val armorStand = EntityType.ARMOR_STAND.entityClass?.let { entity.world.spawn(popupLoc, it) { stand ->
            if (stand is ArmorStand) {
                stand.isVisible = false
                stand.isMarker = true
                stand.isSmall = true
                stand.isSilent = true
                stand.setGravity(false)
                stand.setAI(false)
                stand.isCustomNameVisible = true
                stand.customName(Component.text(amount, if (isDamage) NamedTextColor.RED else NamedTextColor.GREEN))
            }
        }} as ArmorStand
        object : BukkitRunnable() {
            override fun run() {
                armorStand.remove()
            }
        }.runTaskLater(main, 30)
        popupStands.add(armorStand)
    }

    fun clearPopup() {
        popupStands.forEach {
            it.remove()
        }
    }
}