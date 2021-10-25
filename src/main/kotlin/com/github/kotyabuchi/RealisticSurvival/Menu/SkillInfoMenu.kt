package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.PassiveSkillInfoButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.SkillInfoButton
import com.github.kotyabuchi.RealisticSurvival.Utility.upperCamelCase
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import kotlin.math.ceil

class SkillInfoMenu(val player: Player, val job: JobMaster, val isPassiveSkill: Boolean = false): Menu(Component.text("${job.jobName.upperCamelCase()} skills"), ceil(job.getSkills().size / 7.0).toInt()) {

    init {
        createMenu()
    }

    override fun createMenu() {
        setFrame()
        if (isPassiveSkill) {
            job.getPassiveSkills().forEach { skill ->
                setMenuButton(PassiveSkillInfoButton(player, skill, this))
            }
        } else {
            job.getSkills().values.forEach { skill ->
                setMenuButton(SkillInfoButton(skill))
            }
        }
    }
}