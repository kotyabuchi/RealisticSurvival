package com.github.kotyabuchi.RealisticSurvival.Menu.ResourceStorage

import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic.BlankButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.Menu.ResourceStorage.Button.StoredResourceButton
import com.github.kotyabuchi.RealisticSurvival.Menu.ResourceStorage.Button.UnlockSlotButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.ResourceStorage
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.addItemOrDrop
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.PlayerInventory
import kotlin.math.min

class ResourceStorageMenu(private val resourceStorage: ResourceStorage): Menu(Component.text("Resource Storage"), resourceStorage.slotSize, FrameType.TOP) {

    init {
        createMenu()
    }

    override fun createMenu() {
        resourceStorage.getStoredResources().forEach { (itemStack, amount) ->
            setMenuButton(StoredResourceButton(itemStack, amount))
        }
        if (resourceStorage.maxSlotSize > resourceStorage.slotSize) setMenuButton(UnlockSlotButton(this, resourceStorage), slot = menuSize - 5)
        setMenuButton(BlankButton(Material.IRON_BARS), slots = 9 + resourceStorage.slotSize until (menuSize - 9))
    }

    override fun doButtonClickEvent(slot: Int, button: MenuButton, event: InventoryClickEvent, page: Int) {
        if (button is StoredResourceButton) {
            val amount = if (event.isShiftClick) {
                min(button.material.maxStackSize, button.totalAmount)
            } else if (event.isRightClick) {
                min(5, button.totalAmount)
            } else if (event.isLeftClick) {
                1
            } else {
                return
            }
            val player = event.whoClicked as? Player ?: return
            val restoredItem = resourceStorage.restoreResource(button.material, amount) ?: return
            player.inventory.addItemOrDrop(player, restoredItem)
            refresh()
            player.getStatus().openMenu(this, prev = true)
        } else {
            super.doButtonClickEvent(slot, button, event, page)
        }
    }

    override fun doItemClickEvent(slot: Int, event: InventoryClickEvent, page: Int) {
        if (event.clickedInventory !is PlayerInventory) return
        val player = event.whoClicked as? Player ?: return
        val itemStack = event.currentItem ?: return
        if (itemStack.maxStackSize == 1) return
        val amount = if (event.isLeftClick) {
            min(itemStack.maxStackSize, itemStack.amount)
        } else if (event.isRightClick) {
            min(5, itemStack.amount)
        } else {
            return
        }
        val storedAmount = resourceStorage.storeResource(itemStack.asQuantity(amount))
        itemStack.amount -= storedAmount
        refresh()
        player.getStatus().openMenu(this, prev = true)
    }
}