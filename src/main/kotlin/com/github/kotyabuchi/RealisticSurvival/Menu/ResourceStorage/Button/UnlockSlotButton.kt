package com.github.kotyabuchi.RealisticSurvival.Menu.ResourceStorage.Button

import com.github.kotyabuchi.RealisticSurvival.CustomModelData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.Menu.ResourceStorage.ResourceStorageMenu
import com.github.kotyabuchi.RealisticSurvival.System.Player.ResourceStorage
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.floorInt
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import com.github.kotyabuchi.RealisticSurvival.Utility.sendErrorMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class UnlockSlotButton(private val menu: ResourceStorageMenu, private val resourceStorage: ResourceStorage): MenuButton() {

    private val cost = (resourceStorage.slotSize * 1.5 + 5).floorInt()

    init {
        val lore = mutableListOf<Component>()
        lore.add(Component.text("Cost: ", ButtonData.buttonLoreStyle).append(Component.text(cost).normalize()))
        menuIcon = ButtonItem(Material.ENDER_EYE, Component.text("Unlock Slot").normalize(NamedTextColor.GREEN), lore = lore, modelData = CustomModelData.PLUS)
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        if (resourceStorage.slotSize >= resourceStorage.maxSlotSize) return
        val player = event.whoClicked as? Player ?: return
        if (player.level < cost) {
            player.sendErrorMessage("レベルが足りません。")
            return
        }
        player.level -= cost
        resourceStorage.slotSize++
        val resourceStorageMenu = ResourceStorageMenu(resourceStorage)
        resourceStorageMenu.setPrevMenu(menu.getPrevMenu())
        player.getStatus().openMenu(resourceStorageMenu, prev = true)
    }
}