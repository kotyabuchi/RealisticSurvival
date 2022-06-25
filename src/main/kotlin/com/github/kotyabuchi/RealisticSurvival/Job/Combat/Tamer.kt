package com.github.kotyabuchi.RealisticSurvival.Job.Combat

import com.destroystokyo.paper.ParticleBuilder
import com.github.kotyabuchi.RealisticSurvival.CustomPersistentDataType.PersistentDataTypeBoolean
import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.ParticleUtil
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object Tamer: CombatJob("TAMER") {

    private val dyingKey = NamespacedKey(main, "DYING")
    private val sneakingPlayers = mutableMapOf<Player, BukkitTask>()

    @EventHandler
    fun onDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        val entity = event.entity as? LivingEntity ?: return
        val damager = event.damager as? Tameable ?: return

        if (!damager.isTamed) return
        val player = damager.owner as? Player ?: return

        val damage = event.damage
        val exp = getExp(entity, damage / 2)
        player.getStatus().addJobExp(main, this, exp)
    }

    @EventHandler
    fun onDeath(event: EntityDeathEvent) {
        if (event.isCancelled) return
        val entity = event.entity as? Tameable ?: return

        if (!entity.isTamed) return
        event.isCancelled = true
        dying(entity)
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.isCancelled) return
        val entity = event.entity as? Tameable ?: return
        if (isDying(entity)) event.isCancelled = true
    }

    @EventHandler
    fun onSneak(event: PlayerToggleSneakEvent) {
        val player = event.player

        if (event.isSneaking) {
            val nearPet = player.getNearbyEntities(2.0, 1.0, 2.0).filterIsInstance<Tameable>().filter { it.owner == player }.filter { isDying(it) }
            if (nearPet.isEmpty()) return
            sneakingPlayers[player]?.cancel()
            sneakingPlayers[player] = object : BukkitRunnable() {
                var count = 0
                val particleBuilder = ParticleBuilder(Particle.CLOUD)
                    .location(player.location)
                    .receivers(20)
                    .count(3)
                    .offset(.5, .5, .5)
                    .extra(.0)
                override fun run() {
                    if (count >= 20 * 3) {
                        cancel()
                        nearPet.forEach {
                            unDying(it)
                        }
                    } else {
                        nearPet.forEach {
                            particleBuilder.location(it.location.add(.0, it.eyeHeight / 2, .0)).spawn()
                        }
                    }
                    count++
                }
            }.runTaskTimer(main, 0, 1)
        } else {
            sneakingPlayers[player]?.cancel()
            sneakingPlayers.remove(player)
        }
    }

    @EventHandler
    fun onRegain(event: EntityRegainHealthEvent) {
        if (event.isCancelled) return
        val entity = event.entity as? Tameable ?: return
        if (isDying(entity)) unDying(entity)
    }

    @EventHandler
    fun onTarget(event: EntityTargetLivingEntityEvent) {
        val entity = event.target as? Tameable ?: return
        if (isDying(entity)) event.isCancelled = true
    }

    override fun levelUpEvent(player: Player) {
        val particleBuilder = ParticleBuilder(Particle.HEART)
            .location(player.location)
            .receivers(30)
            .count(5)
            .offset(.5, .3, .5)
        for (entity in player.world.getEntitiesByClasses(Tameable::class.java)) {
            if (entity !is Tameable) continue
            if (entity.owner != player) continue
            val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: continue
            val regainAmount = maxHealth - entity.health
            entity.health = maxHealth
            particleBuilder.location(entity.location.add(.0, 1.0, .0)).spawn()
            CustomEventCaller.callEvent(EntityRegainHealthEvent(entity, regainAmount, EntityRegainHealthEvent.RegainReason.MAGIC, true))
            if (isDying(entity)) unDying(entity)
        }
    }

    private fun isDying(entity: Tameable): Boolean {
        return entity.persistentDataContainer.has(dyingKey, PersistentDataTypeBoolean)
    }

    private fun dying(entity: Tameable) {
        entity.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, Int.MAX_VALUE, 1, true, false))
        entity.setAI(false)
        entity.persistentDataContainer.set(dyingKey, PersistentDataTypeBoolean, true)
        if (entity is Sittable) entity.isSitting = true

        for (nearEntity in entity.getNearbyEntities(30.0, 10.0, 30.0)) {
            if (nearEntity !is Mob) return
            if (nearEntity.target == entity) nearEntity.target = null
        }
    }

    private fun unDying(entity: Tameable) {
        entity.persistentDataContainer.remove(dyingKey)
        entity.setAI(true)
        entity.removePotionEffect(PotionEffectType.GLOWING)
        if (entity is Sittable) entity.isSitting = false

        ParticleBuilder(Particle.VILLAGER_HAPPY)
            .location(entity.location.add(.0, 1.0, .0))
            .receivers(30)
            .count(5)
            .offset(.5, .3, .5)
            .spawn()
        entity.world.playSound(entity.location, Sound.ENTITY_PLAYER_LEVELUP, .7f, 1.3f)
    }
}