package com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Event.BlockMineEvent
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.GatheringJob
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.PassiveSkill
import com.github.kotyabuchi.RealisticSurvival.Utility.consume
import com.github.kotyabuchi.RealisticSurvival.Utility.reverse
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject
import java.util.*
import kotlin.math.floor

class TunnelAssist(override val ownerJob: GatheringJob) : PassiveSkill {
    override val main: Main by inject()
    override val skillName: String = "TUNNEL_ASSIST"
    override val displayName: String = "Tunnel Assist"
    override val cost: Int = 0
    override val needLevel: Int = 100
    override var description: String = "[Mine Assist]使用時にトンネル状に外壁を作成する"
    override val coolTime: Long = 0
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

    @EventHandler
    fun onMine(event: BlockMineEvent) {
        if (event.isCancelled) return
        if (!isEnabledSkill(event.player)) return
        if (!event.isMultiBreak) return
        if (!event.isMainBlock) return
        if (event.isMineAssist) return

        val itemStack = event.itemStack

        if (!ownerJob.isJobTool(itemStack.type)) return

        val block = event.block
        val player = event.player
        val level = getSkillLevel(player) ?: return
        val distance = if (level == 50) 3 else floor((level - 50) / 150.0).toInt() + 3

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
                yRange = -2 until distance * 2 - 1
                zRange = (distance * -1) .. distance
            }
            BlockFace.SOUTH, BlockFace.NORTH -> {
                xRange = (distance * -1) .. distance
                yRange = -2 until distance * 2 - 1
                zRange = 0 until 1
            }
            else -> return
        }

        val inventory = player.inventory
        val checkXRange = xRange.count() > 1
        val checkYRange = yRange.count() > 1
        val checkZRange = zRange.count() > 1
        val reveredFace = targetBlockFace.reverse()
        for (x in xRange) {
            val isXWall = checkXRange && (x == xRange.first || x == xRange.last)
            for (y in yRange) {
                val isYWall = checkYRange && (y == yRange.first || y == yRange.last)
                for (z in zRange) {
                    val isZWall = checkZRange && (z == zRange.first || z == zRange.last)
                    val checkBlock = block.getRelative(x, y, z)
                    if (isXWall || isYWall || isZWall) {
                        placeBlockWithConsume(checkBlock, inventory)
                    } else {
                        placeBlockWithConsume(checkBlock.getRelative(reveredFace), inventory)
                    }
                }
            }
        }
    }

    private fun placeBlockWithConsume(block: Block, inventory: Inventory) {
        if (!block.type.isAir && !block.isLiquid) return
        val fillBlock = if (block.y <= 0 && inventory.consume(ItemStack(Material.DEEPSLATE))) {
            Material.DEEPSLATE
        } else if (block.y <= 0 && inventory.consume(ItemStack(Material.COBBLED_DEEPSLATE))) {
            Material.COBBLED_DEEPSLATE
        } else if (inventory.consume(ItemStack(Material.STONE))) {
            Material.STONE
        } else if (inventory.consume(ItemStack(Material.COBBLESTONE))) {
            Material.COBBLESTONE
        } else {
            return
        }
        block.type = fillBlock
    }
}