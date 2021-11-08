package com.github.kotyabuchi.RealisticSurvival.Skill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Event.BlockMineEvent
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.ToggleSkill
import com.github.kotyabuchi.RealisticSurvival.Utility.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.koin.core.component.inject
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

object MineAssist: ToggleSkill {
    override val main: Main by inject()
    override val skillName: String = "MineAssist"
    override val cost: Int = 0
    override val needLevel: Int = 0
    override var description: String = "周囲の鉱石もまとめて採掘する"
    override val coolTime: Long = 0
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

    @EventHandler
    fun onBlockBreak(event: BlockMineEvent) {
        if (event.isCancelled) return
        if (event.isMineAssist) return

        val player = event.player

        if (!isEnabledSkill(player)) return

        val block = event.block
        val itemStack = player.inventory.itemInMainHand
        if (!itemStack.type.isPickAxe()) return
        if (!block.type.isOre()) return

        val ores: MutableList<Block> = mutableListOf()
        searchOres(block, ores, mutableListOf())

        ores.forEach {
            it.miningWithEvent(main, player, itemStack, block, false, isMineAssist = true)
        }
        player.foodLevel = max(0, player.foodLevel - ceil(ores.size / 10.0).toInt())
        itemStack.damage(player, ores.size)
    }

    private fun searchOres(checkBlock: Block, oreList: MutableList<Block>, checkedList: MutableList<Block>) {
        if (checkedList.contains(checkBlock)) return
        checkedList.add(checkBlock)
        val checkMaterial = checkBlock.type
        if (!checkMaterial.isOre()) return
        oreList.add(checkBlock)
        if (checkedList.size > 496) return
        val upBlock = checkBlock.getRelative(BlockFace.UP)
        val downBlock = checkBlock.getRelative(BlockFace.DOWN)
        BlockUtil.aroundBlockFace.forEach {
            searchOres(upBlock.getRelative(it), oreList, checkedList)
        }
        BlockUtil.aroundBlockFace.forEach {
            searchOres(checkBlock.getRelative(it), oreList, checkedList)
        }
        BlockUtil.aroundBlockFace.forEach {
            searchOres(downBlock.getRelative(it), oreList, checkedList)
        }
    }
}