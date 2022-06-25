package com.github.kotyabuchi.RealisticSurvival.Monster

import com.destroystokyo.paper.ParticleBuilder
import com.destroystokyo.paper.event.entity.WitchReadyPotionEvent
import com.destroystokyo.paper.event.entity.WitchThrowPotionEvent
import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Monster.Action.FightingAction
import com.github.kotyabuchi.RealisticSurvival.Utility.ParticleUtil
import com.github.kotyabuchi.RealisticSurvival.Utility.isUndead
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import org.bukkit.scheduler.BukkitRunnable

object AdvWitch: AdvancedMonster(EntityType.WITCH) {

    private val effectLocs = ParticleUtil.circleLocations(1.0, 18)

    init {
        addFightingAction(FightingAction(main, 20L, .1) { mob ->
            if (mob !is Witch) return@FightingAction
            do {
                val actionType = WitchActionType.values().random()
                var fail = false
                when (actionType) {
                    WitchActionType.ENCHANT_CREEPER -> {
                        val creepers = mob.location.getNearbyEntitiesByType(Creeper::class.java, 10.0, 5.0, 10.0).toMutableList().filter { !it.isPowered }
                        if (creepers.isEmpty()) fail = true
                        object : BukkitRunnable() {
                            var count = 0
                            override fun run() {
                                if (count >= creepers.size) {
                                    cancel()
                                } else {
                                    val creeper = creepers[count]
                                    val world = creeper.world
                                    world.strikeLightningEffect(creeper.location)
                                    world.playSound(creeper.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 1f)
                                    creeper.isPowered = true
                                }
                                count++
                            }
                        }.runTaskTimer(main, 0, 10)
                    }
                    WitchActionType.ENCHANT_SPEED -> {
                        addPotionEffectNearMonster(mob, PotionEffect(PotionEffectType.SPEED, 20 * 30, 1), Color.AQUA)
                    }
                    WitchActionType.ENCHANT_STRENGTH -> {
                        addPotionEffectNearMonster(mob, PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 1), Color.RED)
                    }
                    WitchActionType.THUNDER_BOLT -> {
                        mob.target?.let { target ->
                            target.world.strikeLightning(target.location)
                            (target as? Mob)?.target = mob
                        } ?: run {
                            fail = true
                        }
                    }
                }
            } while (fail)
        })
    }

    private fun addPotionEffectNearMonster(witch: Monster, potionEffect: PotionEffect, color: Color) {
        var monster = witch.location.getNearbyEntitiesByType(Monster::class.java, 10.0, 5.0, 10.0).filterNot { it.target == witch || it.hasPotionEffect(potionEffect.type) }.toMutableList()
        monster.remove(witch.target)
        if (monster.size > 3) monster = monster.subList(0, 3)
        monster.add(witch)
        object : BukkitRunnable() {
            var count = 0
            val particleBuilder = ParticleBuilder(Particle.REDSTONE)
                .color(color)
                .location(witch.location)
                .receivers(30)
                .count(1)
            override fun run() {
                if (count >= effectLocs.size) {
                    monster.forEach {
                        it.addPotionEffect(potionEffect)
                    }
                    cancel()
                } else {
                    monster.forEach { monster ->
                        effectLocs.forEach {
                            particleBuilder.location(monster.location.add(it.first, .0, it.second)).spawn()
                        }
                        listOf(effectLocs[count * 2 % effectLocs.size], effectLocs[(count * 2 + 1) % effectLocs.size], effectLocs[(count * 2 + 2) % effectLocs.size]).forEach {
                            particleBuilder.location(monster.location.add(it.first, count * .115, it.second)).spawn()
                        }
                    }
                }
                count++
            }
        }.runTaskTimer(main, 0, 1)
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!entityTypes.contains(event.entityType)) return
        val entity = event.entity as? Mob ?: return
        val damager = event.damager as? LivingEntity ?: return
        entity.location.getNearbyEntitiesByType(Witch::class.java, 10.0).forEach {
            if (it.target == null) {
                it.target = damager
                CustomEventCaller.callEvent(EntityTargetLivingEntityEvent(entity, damager, EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY))
            }
        }
    }

    @EventHandler
    fun onReadyPotion(event: WitchReadyPotionEvent) {
        val witch = event.entity
        val potion = event.potion ?: return
        val potionMeta = potion.itemMeta as? PotionMeta ?: return
        val basePotionType = potionMeta.basePotionData.type
        if (witch.hasPotionEffect(PotionEffectType.POISON) ||
                witch.hasPotionEffect(PotionEffectType.WITHER) ||
                witch.hasPotionEffect(PotionEffectType.SLOW) ||
                witch.hasPotionEffect(PotionEffectType.LEVITATION)) {
            event.potion = ItemStack(Material.MILK_BUCKET)
            object : BukkitRunnable() {
                override fun run() {
                    witch.activePotionEffects.forEach {
                        witch.removePotionEffect(it.type)
                    }
                }
            }.runTaskLater(main, 30)
        } else if (basePotionType == PotionType.INSTANT_HEAL) {
            val maxHealthAttribute = witch.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
            if (maxHealthAttribute.value / 2 <= witch.health) {
                if (!witch.hasPotionEffect(PotionEffectType.REGENERATION)) {
                    potionMeta.basePotionData = PotionData(PotionType.REGEN)
                    potion.itemMeta = potionMeta
                    event.potion = potion
                } else if (!witch.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                    val newPotion = ItemStack(Material.POTION)
                    val newPotionMeta = newPotion.itemMeta as? PotionMeta ?: return
                    newPotionMeta.color = Color.GRAY
                    newPotionMeta.addCustomEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 30, 1), true)
                    newPotion.itemMeta = newPotionMeta
                    event.potion = newPotion
                }
            } else if (!witch.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                potionMeta.basePotionData = PotionData(PotionType.INVISIBILITY)
                potion.itemMeta = potionMeta
                event.potion = potion
            }
        }
    }

    @EventHandler
    fun onThrowPotion(event: WitchThrowPotionEvent) {
        if (!event.target.isUndead()) return
        val potion = event.potion ?: return
        val meta = potion.itemMeta as? PotionMeta ?: return
        val basePotionType = meta.basePotionData.type
        if (basePotionType == PotionType.POISON || basePotionType == PotionType.INSTANT_DAMAGE) {
            meta.basePotionData = PotionData(PotionType.INSTANT_HEAL)
            potion.itemMeta = meta
            event.potion = potion
        }
    }

    enum class WitchActionType {
        ENCHANT_CREEPER,
        ENCHANT_SPEED,
        ENCHANT_STRENGTH,
        THUNDER_BOLT,
    }
}