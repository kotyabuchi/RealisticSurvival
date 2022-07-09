package com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Event.GatheringEvent
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.GatheringJob
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.PassiveSkill
import com.github.kotyabuchi.RealisticSurvival.System.Player.getJobLevel
import org.bukkit.event.EventHandler
import org.koin.core.component.inject
import java.util.*
import kotlin.math.min
import kotlin.random.Random

class Archaeologist(override val ownerJob: GatheringJob): PassiveSkill {
    override val main: Main by inject()
    override val skillName: String = "ARCHAEOLOGIST"
    override val displayName: String = "Archaeologist"
    override val needLevel: Int = 20
    override var description: String = "土や砂を破壊した際に低確率で希少なアイテムがドロップする"
    override val coolTime: Long = 0
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

//    private val dropTable = RandomTable<Material>()

    init {
//        dropTable.addItem(Material.BONE, 10)
//            .addItem(Material.NAME_TAG, 3)
//            .addItem(Material.REDSTONE, 3)
//            .addItem(Material.GLOWSTONE, 2)
//            .addItem(Material.DIAMOND, 1)
//            .generate()
    }

    @EventHandler
    fun onBreak(event: GatheringEvent) {
        val player = event.player
        val block = event.block

        if (!ownerJob.isTargetBlock(block)) return
        if (!isEnabledSkill(player)) return
        if (event.jobMaster != ownerJob) return
        val level = player.getJobLevel(ownerJob)
        val dropLocation = block.location.toCenterLocation()

        if (Random.nextInt(1000) >= min(50.0, level * .5)) return
//        dropTable.getRandom()?.let {
//            dropLocation.world.dropItem(dropLocation, ItemStack(it))
//        }
    }
}