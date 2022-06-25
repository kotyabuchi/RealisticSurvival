package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.Event.PlayerInteractBlockEvent
import com.github.kotyabuchi.RealisticSurvival.Utility.replaceNull
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import kotlin.math.min

object SortChest: CommandExecutor, Listener {

    private val sortPlayer = mutableListOf<Player>()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("ゲーム内でのみ使用できるコマンドです"))
            return true
        }
        sortPlayer.add(sender)
        sender.sendMessage(Component.text("チェストを右クリックでソート"))
        return true
    }

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        if (event.isCancelled) return
        if (event is PlayerInteractBlockEvent) return
        if (event.hand != EquipmentSlot.HAND) return
        val player = event.player
        if (!sortPlayer.contains(player)) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            cancelSortMode(player)
            return
        }
        val block = event.clickedBlock ?: return
        val chest = block.state as? Chest ?: return
        val inventory = chest.inventory
        var content = inventory.storageContents.toMutableList().replaceNull(ItemStack(Material.AIR))
        content = sort(content)
        chest.inventory.storageContents = content.toTypedArray()
        event.isCancelled = true
        sortPlayer.remove(player)
        player.sendMessage(Component.text("ソート完了", NamedTextColor.GREEN))
        player.playSound(block.location.toCenterLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.3f)
    }

    @EventHandler
    fun onClickInv(event: InventoryClickEvent) {
        if (event.isCancelled) return
        val player = event.whoClicked as? Player ?: return
        val inv = event.clickedInventory ?: return
        val type = inv.type

        if (type != InventoryType.PLAYER &&
                type != InventoryType.CHEST &&
                type != InventoryType.ENDER_CHEST &&
                type != InventoryType.BARREL &&
                type != InventoryType.SHULKER_BOX &&
                type != InventoryType.DISPENSER &&
                type != InventoryType.DROPPER) return

        when (event.click) {
            ClickType.MIDDLE -> {
                if (player.gameMode == GameMode.CREATIVE) return
                if (inv.type == InventoryType.PLAYER) return
                if (event.currentItem?.type?.isAir == false) return
            }
            ClickType.DOUBLE_CLICK -> {
                if (event.currentItem?.type?.isAir == false) return
                if (event.cursor?.type?.isAir == false) return
            }
            else -> return
        }

        var content = inv.storageContents.toMutableList().replaceNull(ItemStack(Material.AIR))
        var header = listOf<ItemStack>()
        var footer = listOf<ItemStack>()
        if (inv is PlayerInventory) {
            header = content.take(9)
            footer = content.drop(36)
            content = content.drop(9).take(27)
        }
        content = sort(content)
        inv.storageContents = header.toTypedArray() + content.toTypedArray() + footer.toTypedArray()
        player.playSound(player.eyeLocation, Sound.UI_BUTTON_CLICK, 1f ,1f)
    }

    private fun sort(_content: List<ItemStack>): List<ItemStack> {
        var content = _content.toMutableList()
        val comparator = compareBy<ItemStack> { it.type }.thenByDescending { it.amount }

        val cache = mutableMapOf<ItemStack, Int>()
        content.forEach {
            if (!it.type.isAir) {
                val asOne = it.asOne()
                cache[asOne] = (cache[asOne] ?: 0) + it.amount
            }
        }
        val newContent = mutableListOf<ItemStack>()
        cache.forEach { (itemStack, _amount) ->
            var amount = _amount
            while (amount > 0) {
                val addAmount = min(itemStack.maxStackSize, amount)
                itemStack.amount = addAmount
                newContent.add(itemStack.clone())
                amount -= addAmount
            }
        }
        content = newContent
        content.sortWith(nullsLast(comparator))

        repeat(_content.size - content.size) {
            content.add(ItemStack(Material.AIR))
        }
        return content
    }

    private fun cancelSortMode(player: Player) {
        sortPlayer.remove(player)
        player.sendMessage(Component.text("ソートをキャンセルしました", NamedTextColor.RED))
        player.playSound(player.eyeLocation, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1.5f)
    }
}