package com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Event.GatheringEvent
import com.github.kotyabuchi.RealisticSurvival.Job.GatheringJob
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.PassiveSkill
import com.github.kotyabuchi.RealisticSurvival.System.Player.getJobLevel
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject
import java.util.*
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random

class GemCollector(override val ownerJob: GatheringJob): PassiveSkill {
    override val main: Main by inject()
    override val skillName: String = "GemCollector"
    override val cost: Int = 0
    override val needLevel: Int = 0
    override var description: String = "石を破壊した際に低確率で鉱石がドロップする"
    override val coolTime: Long = 0
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

    private val chancePerLevel = mapOf(
        Material.COAL to 0.1,
        Material.RAW_IRON to 0.06,
        Material.RAW_COPPER to 0.07,
        Material.RAW_GOLD to 0.04,
        Material.REDSTONE to 0.03,
        Material.LAPIS_LAZULI to 0.03,
        Material.DIAMOND to 0.01,
        Material.EMERALD to 0.005
    )

    @EventHandler
    fun onBreak(event: GatheringEvent) {
        val player = event.player
        val block = event.block

        if (block.type != Material.STONE) return
        if (!isEnabledSkill(player)) return
        if (event.jobMaster != ownerJob) return
        val level = player.getJobLevel(ownerJob)
        val dropLocation = block.location.toCenterLocation()

        chancePerLevel.forEach { (material, perLevel) ->
            if (Random.nextInt(10000) < perLevel * min(level, 100) * 100) {
                val amount = floor(perLevel * level / 100).toInt() + 1
                block.world.dropItem(dropLocation, ItemStack(material, amount))
            }
        }
    }
}