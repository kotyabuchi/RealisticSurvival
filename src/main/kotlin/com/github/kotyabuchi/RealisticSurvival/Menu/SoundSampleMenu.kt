package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Event.ChangeMenuPageEvent
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.SoundSampleButton
import net.kyori.adventure.text.Component
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object SoundSampleMenu: Menu(Component.text("Sound Sample"), Sound.values().size), Listener {

    private const val menuContentSize = 9 * 5
    private val createdPages = mutableSetOf<Int>()
    private val soundList = Sound.values().toList().chunked(menuContentSize)

    init {
        createMenu()
    }

    override fun createMenu() {
        createPageIfNeed(soundList.size - 1)
        soundList[0].forEachIndexed { index, sound ->
            setMenuButton(SoundSampleButton(sound), 0, index % menuContentSize)
        }
    }

    @EventHandler
    fun onChangePage(event: ChangeMenuPageEvent) {
        if (!event.isNext) return
        if (event.menu != this) return
        val nextPage = event.nextPage - 1
        if (createdPages.contains(nextPage)) return
        soundList[nextPage].forEachIndexed { index, sound ->
            setMenuButton(SoundSampleButton(sound), nextPage, index % menuContentSize)
        }
        createdPages.add(nextPage)
    }
}