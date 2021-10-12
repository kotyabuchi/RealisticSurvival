package com.github.kotyabuchi.RealisticSurvival.System.Combat

import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

object Fracture: Listener {

    @EventHandler
    fun onFall(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return
        val entity = event.entity as? LivingEntity ?: return
        val damage = event.damage
        val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return
        val ratio = damage / maxHealth
        val rand = Random.nextInt(10) + (ratio * 10)

        val level = if (ratio >= .8) {
            3
        } else if (ratio >= .5 && rand <= 11) {
            2
        } else if (ratio >= .2 && rand <= 11) {
            1
        } else if (ratio > 0 && rand <= 11){
            1
        } else {
            return
        }
        entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 5, level))
    }
}