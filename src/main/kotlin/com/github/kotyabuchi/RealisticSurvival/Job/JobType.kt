package com.github.kotyabuchi.RealisticSurvival.Job

import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.Excavator
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.Farmer
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.Lumberjack
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.Miner
import com.github.kotyabuchi.RealisticSurvival.Utility.upperCamelCase
import org.bukkit.Material

enum class JobType(val jobClass: JobMaster, private val icon: Material, val regularName: String = jobClass.jobName.upperCamelCase()) {
    EXCAVATOR(Excavator, Material.IRON_SHOVEL),
    FARMER(Farmer, Material.STONE_HOE),
    LUMBERJACK(Lumberjack, Material.IRON_AXE),
    MINER(Miner, Material.IRON_PICKAXE),
    ;
    fun getIcon(): Material = icon
}