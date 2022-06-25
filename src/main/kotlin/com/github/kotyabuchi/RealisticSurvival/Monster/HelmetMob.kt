package com.github.kotyabuchi.RealisticSurvival.Monster

import com.github.kotyabuchi.RealisticSurvival.Monster.Action.MobAction
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

open class HelmetMob(
    private val helmetColors: Map<EntityType, Color>,
    vararg entityTypes: EntityType
): AdvancedMonster(*entityTypes) {

    init {
        addSpawnAction(MobAction { entity ->
            val helmetColor = helmetColors[entity.type] ?: return@MobAction
            val equipment = entity.equipment
            if (equipment.helmet?.type == Material.AIR) {
                equipment.helmetDropChance = 0f
                val helmet = ItemStack(Material.LEATHER_HELMET)
                helmet.editMeta { meta ->
                    if (meta is LeatherArmorMeta) meta.setColor(helmetColor)
                }
                equipment.helmet = helmet
            }
        })
    }
}