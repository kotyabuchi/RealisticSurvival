package com.github.kotyabuchi.RealisticSurvival.System.Item

import com.github.kotyabuchi.RealisticSurvival.Event.PrepareItemExtensionCraftEvent
import com.github.kotyabuchi.RealisticSurvival.Utility.isTools
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.math.roundToInt

object CraftExtension: Listener {

    @EventHandler
    fun onCraft(event: PrepareItemExtensionCraftEvent) {
        val result = event.result
        val resultItemStackType = result.itemStack.type
        val inv = event.inventory

        if (!resultItemStackType.isTools()) return
        if (!resultItemStackType.name.startsWith("STONE_")) return
        var allDeepSlate = true
        for (material in inv.matrix) {
            if (material?.type == Material.COBBLESTONE) {
                allDeepSlate = false
                break
            }
        }
        if (allDeepSlate) {
            result.maxDurability((result.maxDurability * 1.5).roundToInt())
            result.durability(result.maxDurability)
            result.applyDurability()
            result.applySetting()
        }
    }
}