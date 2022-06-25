package com.github.kotyabuchi.RealisticSurvival.Monster

import com.github.kotyabuchi.RealisticSurvival.Monster.Action.FightingAction
import com.github.kotyabuchi.RealisticSurvival.Monster.Action.MobAction
import com.github.kotyabuchi.RealisticSurvival.Utility.ParticleUtil
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.EulerAngle

object AdvSkeleton: HelmetMob(
    mapOf(EntityType.SKELETON to Color.fromRGB(236, 240, 241),
        EntityType.STRAY to Color.fromRGB(106, 129, 131)
    ),
    EntityType.SKELETON, EntityType.STRAY) {

    private val weaponBackups = mutableMapOf<Entity, ItemStack>()
    private val boneGuardLocs = ParticleUtil.circleLocations(1.2, 36)
    private val boneGuardMap = mutableMapOf<Mob, MutableList<Entity>>()

    init {
        addFightingAction(FightingAction(main, 5L, 1.0) { mob ->
            val weaponBackup = weaponBackups[mob]

            val target = mob.target ?: return@FightingAction
            val location = mob.location
            val targetLocation = target.location
            val equipment = mob.equipment

            if (weaponBackup == null && (mob.pathfinder.findPath(targetLocation)?.finalPoint?.distance(targetLocation) ?: 100.0) <= 1) {
                if (location.distance(targetLocation) <= 4) {
                    val axe = ItemStack(Material.WOODEN_AXE)
                    weaponBackups[mob] = equipment.itemInMainHand
                    equipment.setItemInMainHand(axe)
                    equipment.itemInMainHandDropChance = 0f
                }
            } else if (weaponBackup != null && location.distance(targetLocation) >= 6) {
                equipment.setItemInMainHand(weaponBackup)
                equipment.itemInMainHandDropChance = 0.085f
                weaponBackups.remove(mob)
            }
        })

        addFightingAction(FightingAction(main, 10L, 0.1, 20 * 10) { mob ->
            if (boneGuardMap.contains(mob)) return@FightingAction

            val boneGuards = mutableListOf<Entity>()
            val baseLoc = mob.location
            baseLoc.yaw = 0f
            baseLoc.pitch = 0f
            repeat(3) { count ->
                val boneGuardLoc = boneGuardLocs[12 * count]
                boneGuards.add(
                    mob.world.spawn(baseLoc.clone().add(boneGuardLoc.first, -.5, boneGuardLoc.second), ArmorStand::class.java) {
                        it.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING)
                        it.equipment.helmet = ItemStack(Material.BONE)
                        it.setAI(false)
                        it.setGravity(false)
                        it.isMarker = true
                        it.isVisible = false
                        it.headPose = EulerAngle(.0, .0, 40.0)
                    }
                )
            }
            boneGuardMap[mob] = boneGuards
            object : BukkitRunnable() {
                var count = 0
                val boneGuardLocAmount = boneGuardLocs.size
                override fun run() {
                    if (boneGuards.isNotEmpty()) {
                        if (mob.isValid) {
                            val rotateBaseLoc = mob.location.clone()
                            rotateBaseLoc.pitch = 0f
                            boneGuards.forEachIndexed { index, boneGuard ->
                                val boneGuardLoc = boneGuardLocs[(boneGuardLocAmount / boneGuards.size * index + count) % boneGuardLocAmount]
                                boneGuard.teleport(rotateBaseLoc.clone().add(boneGuardLoc.first, -.5, boneGuardLoc.second))
                            }
                            count = (count + 1) % boneGuardLocAmount
                        } else {
                            boneGuards.forEach {
                                it.remove()
                            }
                            boneGuardMap.remove(mob)
                        }
                    } else {
                        cancel()
                    }
                }
            }.runTaskTimer(main, 0, 1)
        })

        addFinishedFightAction(MobAction { mob ->
            weaponBackups[mob]?.let {
                mob.equipment.setItemInMainHand(it)
                mob.equipment.itemInMainHandDropChance = 0.085f
            }
            weaponBackups.remove(mob)

            removeBoneGuard(mob)
        })

        addDeathAction(MobAction { mob ->
            weaponBackups.remove(mob)
            removeBoneGuard(mob)
        })
    }

    private fun removeBoneGuard(mob: Mob) {
        boneGuardMap[mob]?.let { boneGuards ->
            boneGuards.forEach {
                it.remove()
            }
            boneGuards.clear()
        }
        boneGuardMap.remove(mob)
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (!entityTypes.contains(event.entityType)) return
        val entity = event.entity as? Mob ?: return
        val boneGuards = boneGuardMap[entity] ?: return
        event.isCancelled = true
        boneGuards.firstOrNull()?.remove()
        boneGuards.removeFirstOrNull()
        entity.world.playSound(entity.location, Sound.ENTITY_SKELETON_HURT, .8f, .6f)
        if (boneGuards.isEmpty()) removeBoneGuard(entity)
    }
}