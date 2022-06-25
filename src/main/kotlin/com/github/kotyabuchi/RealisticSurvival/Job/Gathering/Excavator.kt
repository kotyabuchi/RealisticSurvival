package com.github.kotyabuchi.RealisticSurvival.Job.Gathering

import com.github.kotyabuchi.RealisticSurvival.Skill.Gathering.MultiBreakExcavator
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering.Archaeologist
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering.HadesBlessing
import com.github.kotyabuchi.RealisticSurvival.Skill.SkillCommand
import org.bukkit.Material

object Excavator: GatheringJob("EXCAVATOR") {

    override val canGetExpWithHand: Boolean = true
    private val dirtSet = setOf(
        Material.DIRT, Material.SAND, Material.GRASS_BLOCK, Material.GRAVEL, Material.FARMLAND,
        Material.DIRT_PATH, Material.COARSE_DIRT, Material.PODZOL, Material.RED_SAND, Material.SOUL_SAND, Material.SOUL_SOIL)

    init {
        Material.values().forEach {
            if (it.name.endsWith("_SHOVEL")) addTool(it)
        }
        dirtSet.forEach {
            addExpMap(1, it)
        }
        addExpMap(2, Material.CLAY)

        registerSkill(SkillCommand.RLR, MultiBreakExcavator)
        registerPassiveSkill(Archaeologist(this))
        registerPassiveSkill(HadesBlessing(this))
    }
}