package com.github.kotyabuchi.RealisticSurvival.Job.Gathering

import com.github.kotyabuchi.RealisticSurvival.Job.GatheringJob
import com.github.kotyabuchi.RealisticSurvival.Utility.isHoe
import org.bukkit.Material

object Farmer: GatheringJob("FARMER") {

    init {
        Material.values().forEach {
            if (it.isHoe()) addTool(it)
        }
        addExpMap(1, Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS)
        addExpMap(2, Material.MELON)
        addExpMap(10, Material.PUMPKIN)
    }
}