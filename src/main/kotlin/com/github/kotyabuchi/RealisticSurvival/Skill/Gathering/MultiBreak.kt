package com.github.kotyabuchi.RealisticSurvival.Skill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Event.BlockMineEvent
import com.github.kotyabuchi.RealisticSurvival.Item.Enum.ToolType
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.ToggleSkill
import com.github.kotyabuchi.RealisticSurvival.Utility.damage
import com.github.kotyabuchi.RealisticSurvival.Utility.miningWithEvent
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.koin.core.component.inject
import java.util.*
import kotlin.math.floor

abstract class MultiBreak: ToggleSkill {
    override val main: Main by inject()
    override val displayName: String = "Multi Break"
    override val cost: Int = 0
    override val needLevel: Int = 50
    override var description: String = "周囲のブロックをまとめて採掘する"
    override val coolTime: Long = 0
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

    abstract val targetToolType: ToolType

    @EventHandler
    fun onBlockBreak(event: BlockMineEvent) {
        if (event.isCancelled) return
        if (event.isMineAssist) return
        if (event.isMultiBreak) return

        val player = event.player

        if (!isEnabledSkill(player)) return

        val block = event.block
        val itemStack = player.inventory.itemInMainHand
        if (!targetToolType.includes(itemStack)) return
        if (!block.isPreferredTool(itemStack)) return
        event.isCancelled = true

        val level = getSkillLevel(player) ?: return
        val aroundBlock = mutableListOf<Block>()
        val distance = if (level == 50) 1 else floor((level - 50) / 150.0).toInt() + 1

        val targetBlockFace = player.getTargetBlockFace(6) ?: return
        val xRange: IntRange
        val yRange: IntRange
        val zRange: IntRange
        when (targetBlockFace) {
            BlockFace.UP, BlockFace.DOWN -> {
                xRange = (distance * -1) .. distance
                yRange = 0 until 1
                zRange = (distance * -1) .. distance
            }
            BlockFace.EAST, BlockFace.WEST -> {
                xRange = 0 until 1
                yRange = -1 until distance * 2
                zRange = (distance * -1) .. distance
            }
            BlockFace.SOUTH, BlockFace.NORTH -> {
                xRange = (distance * -1) .. distance
                yRange = -1 until distance * 2
                zRange = 0 until 1
            }
            else -> return
        }

        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
                    val checkBlock = block.getRelative(x, y, z)
                    if (checkBlock.isValidTool(itemStack)) aroundBlock.add(checkBlock)
                }
            }
        }

        aroundBlock.forEach {
            it.miningWithEvent(main, player, itemStack, block, false, isMultiBreak = true)
        }
        itemStack.damage(player, aroundBlock.size)
    }
}