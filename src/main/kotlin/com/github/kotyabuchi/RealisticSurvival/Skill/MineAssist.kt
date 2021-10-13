package com.github.kotyabuchi.RealisticSurvival.Skill

import com.github.kotyabuchi.RealisticSurvival.Event.BlockMineEvent
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.koin.core.component.inject

object MineAssist: ToggleSkill {
    override val main: Main by inject()
    override val skillName: String = "MineAssist"
    override val cost: Int = 0
    override val needLevel: Int = 0
    override var description: String = "周囲の鉱石もまとめて採掘する"

    @EventHandler
    fun onSwitch(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        val item = event.offHandItem ?: return
        if (!item.type.isPickAxe()) return
        event.isCancelled = true
        toggleSkill(player, 1)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event is BlockMineEvent) return
        if (event.isCancelled) return

        val player = event.player

        if (!isEnabledSkill(player)) return

        val block = event.block
        val itemStack = player.inventory.itemInMainHand
        if (!itemStack.type.isPickAxe()) return
        if (!block.type.isOre()) return

        val ores: MutableList<Block> = mutableListOf()
        searchOres(block, ores, mutableListOf())

        ores.forEach {
            it.miningWithEvent(main, player, itemStack, block)
        }
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