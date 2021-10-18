package com.github.kotyabuchi.RealisticSurvival.Job

import com.github.kotyabuchi.RealisticSurvival.Utility.upperCamelCase
import org.bukkit.Material

enum class JobType(val jobClass: JobMaster, val regularName: String = jobClass.jobName.upperCamelCase(), private val icon: Material) {
    ;
    fun getIcon(): Material = icon
}