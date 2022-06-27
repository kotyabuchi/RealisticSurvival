package com.github.kotyabuchi.RealisticSurvival.Menu.Home

import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.HomeIconButton
import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.Home
import com.github.kotyabuchi.RealisticSurvival.System.Player.HomePoint
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class ChangeHomeIconMenu(private val home: Home): Menu(Component.text("Change Icon"), Material.values().size) {

    private val menuContentSize = 9 * 5
    private val createdPages = mutableSetOf<Int>()
    private val materialList = Material.values().toList().chunked(menuContentSize)

    companion object {
        private val iconButtons: MutableMap<Int, List<MenuButton>> = mutableMapOf()
    }

    init {
        createMenu()
    }

    override fun createMenu() {
        createPageIfNeed(materialList.size - 1)
        getIconButtons(0).forEachIndexed { index, menuButton ->
            setMenuButton(menuButton, 0, index % menuContentSize)
        }
    }

    private fun getIconButtons(page: Int): List<MenuButton> {
        return iconButtons.getOrPut(page) {
            val list = mutableListOf<MenuButton>()
            materialList[page].forEach {
                list.add(HomeIconButton(it))
            }
            list
        }
    }

    override fun doButtonClickEvent(slot: Int, button: MenuButton, event: InventoryClickEvent, page: Int) {
        super.doButtonClickEvent(slot, button, event, page)
        if (button !is HomeIconButton) return
        val player = event.whoClicked as? Player ?: return
        HomePoint.changeIcon(home, button.iconMaterial)
        backToPrevMenu(player)
    }

    override fun changePageEvent(nextPage: Int, totalPage: Int, isNext: Boolean, player: Player) {
        if (!isNext) return
        if (createdPages.contains(nextPage)) return
        val icons = getIconButtons(nextPage)
        icons.forEachIndexed { index, menuButton ->
            setMenuButton(menuButton, nextPage, index % menuContentSize)
        }
        createdPages.add(nextPage)
    }
}