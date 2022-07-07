package com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Event.BlockMineEvent
import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.GatheringJob
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.PassiveSkill
import com.github.kotyabuchi.RealisticSurvival.Utility.addItemOrDrop
import com.github.kotyabuchi.RealisticSurvival.Utility.breakBlock
import com.github.kotyabuchi.RealisticSurvival.Utility.consume
import com.github.kotyabuchi.RealisticSurvival.Utility.getRemaining
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject
import java.util.*

class StoneReplacer(override val ownerJob: GatheringJob) : PassiveSkill {
    override val main: Main by inject()
    override val skillName: String = "STONE_REPLACER"
    override val displayName: String = "Stone Replacer"
    override val cost: Int = 0
    override val needLevel: Int = 0
    override var description: String = "[Mine Assist]使用時に鉱石を石に置き換える"
    override val coolTime: Long = 0
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

    @EventHandler
    fun onMine(event: BlockMineEvent) {
        val player = event.player

        if (event.isCancelled) return
        if (!isEnabledSkill(event.player)) return
        if (event.isMainBlock && event.isMultiBreak) return
        if (event.isMultiBreak) return
        if (!event.isMineAssist) return

        val block = event.block
        val inventory = player.inventory

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
        event.isCancelled = true
        ownerJob.addBrokenBlockSet(block)

        block.breakBlock(main, player, event.itemStack, event.block, damage = false,
            blockCallBack = {
                it.type = fillBlock
            },
            dropItemCallBack = {
                val items = it.items
                items.forEach { item ->
                    val playerAttemptPickupItemEvent = PlayerAttemptPickupItemEvent(player, item, player.inventory.getRemaining(item.itemStack))
                    CustomEventCaller.callEvent(playerAttemptPickupItemEvent)
                    player.inventory.addItemOrDrop(player, playerAttemptPickupItemEvent.item.itemStack)
                    item.remove()
                }
                items.clear()
            })
    }
}