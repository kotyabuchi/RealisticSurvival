package com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Skill.ToggleSkill
import org.bukkit.entity.Player

interface PassiveSkill: ToggleSkill {

    val ownerJob: JobMaster

    override fun enableAction(player: Player, level: Int) {}
    override fun disableAction(player: Player) {}
}