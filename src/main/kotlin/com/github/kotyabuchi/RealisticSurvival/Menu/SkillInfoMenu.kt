package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.PassiveSkillInfoButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.SkillInfoButton
import com.github.kotyabuchi.RealisticSurvival.Utility.upperCamelCase
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import kotlin.math.max

class SkillInfoMenu(val player: Player, val job: JobMaster, val isPassiveSkill: Boolean = false)
    : Menu(Component.text("${job.jobName.upperCamelCase()} skills"), if (isPassiveSkill) job.getPassiveSkills().size else job.getSkills().values.size, FrameType.TOP, FrameType.SIDE) {

    init {
        createMenu()
    }

    override fun createMenu() {
        if (isPassiveSkill) {
            job.getPassiveSkills().forEach { skill ->
                setMenuButton(PassiveSkillInfoButton(player, skill, this))
            }
        } else {
            job.getSkills().forEach { (command, skill) ->
                setMenuButton(SkillInfoButton(command, skill))
            }
        }
    }
}