package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.SkillInfoButton
import com.github.kotyabuchi.RealisticSurvival.Utility.upperCamelCase
import net.kyori.adventure.text.Component
import kotlin.math.ceil

class SkillInfoMenu(val job: JobMaster): Menu(Component.text("${job.jobName.upperCamelCase()} skills"), ceil(job.getSkills().size / 7.0).toInt()) {

    init {
        createMenu()
    }

    override fun createMenu() {
        setFrame()
        job.getSkills().values.forEach { skill ->
            setMenuButton(SkillInfoButton(skill))
        }
    }
}