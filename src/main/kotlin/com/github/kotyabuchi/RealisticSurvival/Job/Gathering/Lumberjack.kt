package com.github.kotyabuchi.RealisticSurvival.Job.Gathering

import com.github.kotyabuchi.RealisticSurvival.Job.GatheringJob
import com.github.kotyabuchi.RealisticSurvival.Skill.Gathering.TreeAssist
import com.github.kotyabuchi.RealisticSurvival.Skill.SkillCommand
import com.github.kotyabuchi.RealisticSurvival.Utility.isWood
import org.bukkit.Material

object Lumberjack: GatheringJob("LUMBERJACK") {

    init {
        Material.values().forEach {
            if (it.name.endsWith("_AXE")) addTool(it)
            if (it.isWood()) addExpMap(1, it)
        }

        registerSkill(SkillCommand.LRL, TreeAssist)
    }
}