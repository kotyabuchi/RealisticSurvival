package com.github.kotyabuchi.RealisticSurvival.Monster.CustomMonster

import net.kyori.adventure.sound.Sound
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.persistence.PersistentDataType

abstract class CustomEntity: Listener {

    abstract val nameKey: NamespacedKey
    abstract val damageSound: Sound

    open fun spawn(owner: LivingEntity): LivingEntity? = null

    open fun spawn(location: Location): LivingEntity? = null

    @EventHandler
    fun playDamageSound(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity.persistentDataContainer.has(nameKey, PersistentDataType.BYTE)) entity.world.playSound(damageSound)
    }
}