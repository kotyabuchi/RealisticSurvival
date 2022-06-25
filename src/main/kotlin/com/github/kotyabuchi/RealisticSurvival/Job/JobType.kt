package com.github.kotyabuchi.RealisticSurvival.Job

import com.github.kotyabuchi.RealisticSurvival.Job.Combat.BattleAxe
import com.github.kotyabuchi.RealisticSurvival.Job.Combat.SwordMaster
import com.github.kotyabuchi.RealisticSurvival.Job.Combat.Tamer
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.Excavator
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.Farmer
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.Lumberjack
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.Miner
import com.github.kotyabuchi.RealisticSurvival.Utility.upperCamelCase
import org.bukkit.Material

enum class JobType(val jobClass: JobMaster, private val icon: Material, val jobCategory: JobCategory, val regularName: String = jobClass.jobName.upperCamelCase()) {
    MINER(Miner, Material.STONE_PICKAXE, JobCategory.GATHERING),
    EXCAVATOR(Excavator, Material.STONE_SHOVEL, JobCategory.GATHERING),
    LUMBERJACK(Lumberjack, Material.STONE_AXE, JobCategory.GATHERING),
    FARMER(Farmer, Material.STONE_HOE, JobCategory.GATHERING),
//    SWORD_MASTER(SwordMaster, Material.IRON_SWORD, JobCategory.COMBAT),
//    BATTLE_AXE(BattleAxe, Material.IRON_AXE, JobCategory.COMBAT),
//    TAMER(Tamer, Material.BONE, JobCategory.COMBAT),
    ;
    fun getIcon(): Material = icon
}