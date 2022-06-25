package com.github.kotyabuchi.RealisticSurvival.Job.Combat

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Main
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.sqrt

open class CombatJob(jobName: String): JobMaster(jobName), KoinComponent {

    val main: Main by inject()

    fun getExp(entity: LivingEntity, damage: Double): Double {
        val armor = entity.getAttribute(Attribute.GENERIC_ARMOR)?.value ?: 0.0
        val armorToughness = (entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)?.value ?: 0.0) * 2
        val attackDamage = (entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.value ?: 0.0) * 10
        val attackKnockBack = entity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK)?.value ?: 0.0
        val knockBackResistance = entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)?.value ?: 0.0
        val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0
        val movementSpeed = (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.value ?: 0.0) * 10

        val baseExp = armor + armorToughness + attackDamage + attackKnockBack + knockBackResistance + maxHealth + movementSpeed
        return sqrt(damage) * (baseExp / 10)
    }
}