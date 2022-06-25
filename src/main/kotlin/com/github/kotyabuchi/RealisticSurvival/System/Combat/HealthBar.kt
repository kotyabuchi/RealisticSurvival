package com.github.kotyabuchi.RealisticSurvival.System.Combat

import com.github.kotyabuchi.RealisticSurvival.CustomPersistentDataType.PersistentDataTypeBoolean
import com.github.kotyabuchi.RealisticSurvival.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.max
import kotlin.math.round

object HealthBar: Listener, KoinComponent {

    private val main: Main by inject()
    private val healthBarKey = NamespacedKey(main, "HealthBar")

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamage(event: EntityDamageEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val spawnLoc = entity.location.clone().set(.0, -100.0, .0)
        val health = max(0, round(entity.health - event.finalDamage).toInt())
        val maxHealth = round(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return).toInt()

        val healthBar = entity.passengers.firstOrNull {
            it is ArmorStand && it.persistentDataContainer.has(healthBarKey, PersistentDataTypeBoolean)
        } ?: entity.world.spawn(spawnLoc, ArmorStand::class.java) {
            it.persistentDataContainer.set(healthBarKey, PersistentDataTypeBoolean, true)
            it.isCustomNameVisible = true
            it.setAI(false)
            it.setGravity(false)
            it.isVisible = false
            it.isMarker = true
            entity.addPassenger(it)
        }

        healthBar.customName(Component.text("|".repeat(health), NamedTextColor.GREEN, TextDecoration.BOLD).append(Component.text("|".repeat(maxHealth - health), NamedTextColor.GRAY, TextDecoration.BOLD)))
    }

    @EventHandler
    fun onRegen(event: EntityRegainHealthEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val spawnLoc = entity.location.clone().set(.0, -100.0, .0)
        val health = max(0, round(entity.health - event.amount).toInt())
        val maxHealth = round(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return).toInt()

        val healthBar = entity.passengers.firstOrNull {
            it is ArmorStand && it.persistentDataContainer.has(healthBarKey, PersistentDataTypeBoolean)
        } ?: entity.world.spawn(spawnLoc, ArmorStand::class.java) {
            it.persistentDataContainer.set(healthBarKey, PersistentDataTypeBoolean, true)
            it.isCustomNameVisible = true
            it.setAI(false)
            it.setGravity(false)
            it.isVisible = false
            it.isMarker = true
            entity.addPassenger(it)
        }

        if (health == maxHealth) {
            healthBar.remove()
        } else {
            healthBar.customName(Component.text("|".repeat(health), NamedTextColor.GREEN, TextDecoration.BOLD).append(Component.text("|".repeat(maxHealth - health), NamedTextColor.GRAY, TextDecoration.BOLD)))
        }

    }

    @EventHandler
    fun onDeath(event: EntityDeathEvent) {
        val entity = event.entity as? LivingEntity ?: return
        entity.passengers.filter { it is ArmorStand && it.persistentDataContainer.has(healthBarKey, PersistentDataTypeBoolean) }.forEach {
            it.remove()
        }
    }
}