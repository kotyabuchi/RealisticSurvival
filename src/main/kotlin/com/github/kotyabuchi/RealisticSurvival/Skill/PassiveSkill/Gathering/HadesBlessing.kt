package com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.ToolLinkedEffectSkill
import com.github.kotyabuchi.RealisticSurvival.System.Player.getJobLevel
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.koin.core.component.inject
import java.util.*
import kotlin.math.floor

class HadesBlessing(override val ownerJob: JobMaster): ToolLinkedEffectSkill() {
    override val main: Main by inject()
    override val skillName: String = "HADES_BLESSING"
    override val displayName: String = "Hades Blessing"
    override val needLevel: Int = 20
    override var description: String = "採掘速度が上昇する"
    override val coolTime: Long = 0
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()
    override val targetToolType: Set<Material> = ownerJob.getTool().toSet()

    init {
        startCycle()
    }

    override fun applyEffect(player: Player) {
        val level = floor((player.getJobLevel(ownerJob) - 20) / 20.0).toInt() + 1
        player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 30, level, true, false, false))
    }
}