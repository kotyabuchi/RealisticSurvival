package com.github.kotyabuchi.RealisticSurvival.Job.Combat

import com.github.kotyabuchi.RealisticSurvival.Item.Enum.ToolType
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

abstract class WeaponCombatJob(jobName: String): CombatJob(jobName) {

    abstract val weaponType: ToolType
    abstract val needWeapon: Boolean

    @EventHandler(priority = EventPriority.HIGH)
    fun onDamageHit(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        val entity = event.entity as? LivingEntity ?: return
        val player = event.damager as? Player ?: return
        val item = player.inventory.itemInMainHand

        if (needWeapon) {
            if (!weaponType.includes(item)) return
        } else if (item.type.isAir) return

        val damage = event.damage
        val exp = getExp(entity, damage)

        player.getStatus().addJobExp(main, this, exp)
    }
}