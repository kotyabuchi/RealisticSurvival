package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.Event.PlayerInteractBlockEvent
import com.github.kotyabuchi.RealisticSurvival.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

object SortChest: CommandExecutor, TabCompleter, Listener, KoinComponent {

    private val main: Main by inject()

    private val args1List = listOf("restack")

    private val sortPlayer = mutableListOf<Player>()
    private val sortWithRestackPlayer = mutableListOf<Player>()

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val result = mutableListOf<String>()
        when (args.size) {
            1 -> {
                args1List.forEach {
                    if (it.contains(args[0], true)) result.add(it)
                }
            }
        }
        return result
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("ゲーム内でのみ使用できるコマンドです"))
            return true
        }
        if (args.size == 1 && args[0].equals("restack", true)) {
            sortWithRestackPlayer.add(sender)
        } else {
            sortPlayer.add(sender)
        }
        sender.sendMessage(Component.text("チェストを右クリックでソート"))
        return true
    }

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        if (event is PlayerInteractBlockEvent) return
        if (event.hand != EquipmentSlot.HAND) return
        val player = event.player
        if (!sortPlayer.contains(player) && !sortWithRestackPlayer.contains(player)) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            cancelSortMode(player)
            return
        }
        val block = event.clickedBlock ?: return
        val chest = block.state as? Chest ?: return
        val inventory = chest.inventory
        var content = inventory.contents.toMutableList()
        val comparator = compareBy<ItemStack?> { it?.type }.thenByDescending { it?.amount }

        if (sortWithRestackPlayer.contains(player)) {
            val contentSize = content.size
            val cache = mutableMapOf<ItemStack, Int>()
            content.forEach {
                if (it != null) {
                    val asOne = it.asOne()
                    cache[asOne] = (cache[asOne] ?: 0) + it.amount
                }
            }
            val newContent = mutableListOf<ItemStack?>()
            cache.forEach { (itemStack, _amount) ->
                var amount = _amount
                while (amount > 0) {
                    val addAmount = min(itemStack.maxStackSize, amount)
                    itemStack.amount = addAmount
                    newContent.add(itemStack)
                    amount -= addAmount
                }
            }
            repeat(contentSize - newContent.size) {
                newContent.add(null)
            }
            content = newContent
        }
        content.sortWith(nullsLast(comparator))
        chest.inventory.storageContents = content.toTypedArray()
        event.isCancelled = true
        sortPlayer.remove(player)
        sortWithRestackPlayer.remove(player)
        player.sendMessage(Component.text("ソート完了", NamedTextColor.GREEN))
        player.playSound(block.location.toCenterLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.3f)
    }

    private fun cancelSortMode(player: Player) {
        sortPlayer.remove(player)
        sortWithRestackPlayer.remove(player)
        player.sendMessage(Component.text("ソートをキャンセルしました", NamedTextColor.RED))
        player.playSound(player.eyeLocation, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1.5f)
    }
}