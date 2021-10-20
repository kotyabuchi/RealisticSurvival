package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Event.PlayerInteractBlockEvent
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.addItemOrDrop
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.PlayerInventory

object MenuController: Listener {

    @EventHandler
    fun onClickCompass(event: PlayerInteractEvent) {
        if (event is PlayerInteractBlockEvent) return
        if (event.hand != EquipmentSlot.HAND) return
        val player = event.player
        val inventory = player.inventory
        if (inventory.heldItemSlot != 8) return
        val item = player.inventory.getItem(EquipmentSlot.HAND)
        if (item?.type != Material.COMPASS) return
        event.isCancelled = true
        player.getStatus().openMenu(MainMenu())
    }

    @EventHandler
    fun onClickInvCompass(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem ?: return
        val clickedInventory = event.clickedInventory ?: return
        if (clickedInventory !is PlayerInventory) return
        if (event.slot != 8) return
        if (item.type != Material.COMPASS) return
        event.isCancelled = true
        event.cursor?.let {
            player.inventory.addItemOrDrop(player, it)
            event.cursor = null
        }
        player.getStatus().openMenu(MainMenu())
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val playerStatus = player.getStatus()
        val clickedInventory = event.clickedInventory ?: return
        val openingMenu = playerStatus.getOpeningMenu() ?: return
        val openingPage = playerStatus.getOpeningPage()
        if (clickedInventory == openingMenu.getInventory(openingPage)) {
            if (openingMenu.hasButton(event.rawSlot, openingPage)) {
                event.isCancelled = true
                openingMenu.doButtonClickEvent(event.rawSlot, event, openingPage)
            }
        }
        openingMenu.doItemClickEvent(event.rawSlot, event, openingPage)
    }

    @EventHandler
    fun onCloseMenu(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val status = player.getStatus()
        if (status.openMenuWithCloseMenu) {
            status.openMenuWithCloseMenu = false
        } else {
            status.closeMenu()
        }
    }
}