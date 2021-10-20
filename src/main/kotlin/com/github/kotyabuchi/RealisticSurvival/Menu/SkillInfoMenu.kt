package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.SkillInfoButton
import com.github.kotyabuchi.RealisticSurvival.Utility.upperCamelCase
import net.kyori.adventure.text.Component
import kotlin.math.ceil

class SkillInfoMenu(job: JobMaster): Menu(Component.text("${job.jobName.upperCamelCase()} skills"), ceil(job.getSkills().size / 7.0).toInt()) {

    init {
        setFrame()
        job.getSkills().values.forEach { skill ->
            setMenuButton(SkillInfoButton(skill))
        }
    }
}