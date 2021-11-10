package com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Event.GatheringEvent
import com.github.kotyabuchi.RealisticSurvival.Job.GatheringJob
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.PassiveSkill
import com.github.kotyabuchi.RealisticSurvival.System.Player.getJobLevel
import com.github.kotyabuchi.RealisticSurvival.Utility.RandomTable
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject
import java.util.*
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

    private val dropTable = RandomTable<Material>()

    init {
        dropTable.addItem(Material.COAL, 40)
            .addItem(Material.RAW_IRON, 15)
            .addItem(Material.RAW_COPPER, 20)
            .addItem(Material.RAW_GOLD, 5)
            .addItem(Material.REDSTONE, 5)
            .addItem(Material.LAPIS_LAZULI, 3)
            .addItem(Material.DIAMOND, 1)
            .addItem(Material.EMERALD, 1)
    }

    @EventHandler
    fun onBreak(event: GatheringEvent) {
        val player = event.player
        val block = event.block

        if (block.type != Material.STONE) return
        if (!isEnabledSkill(player)) return
        if (event.jobMaster != ownerJob) return
        val level = player.getJobLevel(ownerJob)
        val dropLocation = block.location.toCenterLocation()

        if (Random.nextInt(1000) >= min(100.0, (level / 100.0))) return
        dropTable.getRandom()?.let {
            dropLocation.world.dropItem(dropLocation, ItemStack(it))
        }
    }
}