package com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Event.BlockMineEvent
import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.PassiveSkill
import com.github.kotyabuchi.RealisticSurvival.Utility.*
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.koin.core.component.inject
import java.util.*

class AutoReplantSapling(override val ownerJob: JobMaster): PassiveSkill {
    override val main: Main by inject()
    override val skillName: String = "AUTO_REPLANT_SAPLING"
    override val displayName: String = "Auto Replant Sapling"
    override val cost: Int = 0
    override val needLevel: Int = 0
    override var description: String = "原木を破壊した際に自動で苗木を植える"
    override val coolTime: Long = 0
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

    @EventHandler
    fun onMine(event: BlockMineEvent) {
        val block = event.block

        if (!block.type.isWood()) return
        if (!block.getRelative(BlockFace.DOWN).type.isDirt()) return

        val woodType = block.getWoodType()
        val saplingType = valueOfOrNull<Material>("${woodType.name}_SAPLING") ?: return

        event.isCancelled = true

        block.breakBlock(main, event.player, event.itemStack, event.block, damage = false) {
            it.type = saplingType
        }
    }
}