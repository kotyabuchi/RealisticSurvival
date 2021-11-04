package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic.BackPageButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic.BackPrevButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic.BlankButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic.NextPageButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.min

abstract class Menu(val title: Component, contentAmount: Int, vararg frames: FrameType) {

    private val hasTopFrame = frames.contains(FrameType.TOP)
    private val hasSideFrame = frames.contains(FrameType.SIDE)
    private val maxContentWidth = if (hasSideFrame) 7.0 else 9.0
    private val maxContentHeight = if (hasTopFrame) 4 else 5
    private val row: Int by lazy { min(maxContentHeight, ceil(contentAmount / maxContentWidth).toInt()) }
    protected val menuSize: Int by lazy { (row + (if (hasTopFrame) 2 else 1)) * 9 }
    private val pages = mutableListOf(getInvTemp())
    private var prevMenu: Menu? = null
    private val buttonItems = mutableListOf(mutableMapOf<Int, MenuButton>())
    val disallowPlayerInventoryClick = true

    init {
        setFooter()
        setFrame()
    }

    fun refresh() {
        pages.clear()
        buttonItems.clear()
        createPageIfNeed(0)
        createMenu()
        setPrevMenu(prevMenu)
    }

    open fun createMenu() {}

    private fun getInvTemp(): Inventory {
        return Bukkit.createInventory(null, menuSize, title)
    }

    private fun setFooter(page: Int = 0) {
        setMenuButton(BlankButton(), page, (menuSize - 9) until menuSize)
    }

    private fun setFrame(page: Int = 0) {
        if (hasTopFrame) setMenuButton(BlankButton(), page, 0..8)
        if (hasSideFrame) {
            repeat(menuSize / 9) {
                setMenuButton(BlankButton(), page, it * 9)
                setMenuButton(BlankButton(), page, it * 9 + 8)
            }
        }
    }

    fun setPrevMenu(menu: Menu?): Menu {
        prevMenu = menu
        menu?.let {
            for (i in 0 until pages.size) {
                setMenuButton(BackPrevButton(menu), i, menuSize - 8)
            }
        }
        return this
    }

    fun setMenuButton(menuButton: MenuButton, page: Int = 0, slots: IntRange, checkNextPage: Boolean = true): Menu {
        slots.forEach {
            setMenuButton(menuButton, page, it, checkNextPage)
        }
        return this
    }

    fun setMenuButton(menuButton: MenuButton, page: Int = 0, slot: Int? = getLastBlankSlot(page), checkNextPage: Boolean = true): Menu {
        if (slot == null) return this
        createPageIfNeed(page)
        buttonItems[page][slot] = menuButton
        pages[page].setItem(slot, menuButton.menuIcon)
        if (checkNextPage && page > 0) {
            setMenuButton(BackPageButton(page, pages.size), page, menuSize - 7, false)
            setMenuButton(NextPageButton(page + 1, pages.size), page - 1, menuSize - 3)
        }
        return this
    }

    fun removeMenuButton(slot: Int, page: Int = 0): Menu {
        buttonItems[page].remove(slot)
        pages[page].setItem(slot, null)
        return this
    }

    fun setItem(slot: Int, item: ItemStack?, page: Int = 0): Menu {
        removeMenuButton(slot, page)
        pages[page].setItem(slot, item)
        return this
    }

    fun removeItem(slot: Int, page: Int = 0): Menu {
        removeMenuButton(slot, page)
        return this
    }

    fun getPrevMenu(): Menu? {
        return prevMenu
    }

    fun getItem(slot: Int, page: Int = 0): ItemStack? {
        return pages[page].getItem(slot)
    }

    fun getInventory(page: Int = 0): Inventory {
        createPageIfNeed(page)
        return pages[page]
    }

    fun getLastBlankSlot(page: Int = 0, passSlot: List<Int> = listOf()): Int? {
        val inventory = getInventory(page)
        for ((index, menuItem) in inventory.withIndex()) {
            if ((menuItem == null || menuItem.type.isAir) && !passSlot.contains(index)) return index
        }
        return null
    }

    fun getSlot(row: Int, column: Int): Int {
        return (row * 9) + column
    }

    fun getButton(slot: Int, page: Int = 0): MenuButton? {
        if (buttonItems.size < page) return null
        return buttonItems[page][slot]
    }

    fun hasButton(slot: Int, page: Int = 0): Boolean {
        if (buttonItems.size < page) return false
        return buttonItems[page].containsKey(slot)
    }

    open fun doButtonClickEvent(slot: Int, event: InventoryClickEvent, page: Int = 0) {
        createPageIfNeed(page)
        val button = getButton(slot, page) ?: return
        button.clickEvent(event)
        val player = event.whoClicked as? Player ?: return
        playClickedButtonSound(button, player)
    }

    open fun doItemClickEvent(slot: Int, event: InventoryClickEvent, page: Int = 0) {}

    open fun doCloseMenuAction(player: Player) {}

    fun playClickedButtonSound(button: MenuButton, player: Player) {
        button.clickSound?.let {
            player.playSound(player.eyeLocation, it, .5f, 1f)
        }
    }

    private fun addPage() {
        pages.add(getInvTemp())
        setFooter(pages.size - 1)
        setFrame(pages.size - 1)
    }

    protected fun createPageIfNeed(page: Int) {
        while (pages.size <= page) {
            addPage()
        }
        while (buttonItems.size <= page) {
            buttonItems.add(mutableMapOf())
        }
    }

    enum class FrameType {
        TOP,
        SIDE,
    }
}