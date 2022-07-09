package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.Menu.SkillInfoMenu
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.PassiveSkill
import com.github.kotyabuchi.RealisticSurvival.System.Player.getJobLevel
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class PassiveSkillInfoButton(val player: Player, val skill: PassiveSkill, val menu: SkillInfoMenu): MenuButton() {

    init {
        val lore = mutableListOf<Component>()
        var state = Component.text("State: ", ButtonData.buttonLoreStyle)
        state = if (skill.isEnabledSkill(player)) {
            state.append(Component.text("Enabled").normalize(NamedTextColor.GREEN))
        } else {
            state.append(Component.text("Disabled").normalize(NamedTextColor.RED))
        }
        lore.add(state)
        lore.add(Component.text("NeedLevel: ", ButtonData.buttonLoreStyle).append(Component.text(skill.needLevel).normalize()))
        lore.add(Component.text("===============", ButtonData.buttonLoreStyle))
        lore.add(Component.text(skill.description, ButtonData.buttonLoreStyle))
        lore.add(Component.empty())
        var clickAction = Component.text("Click: ", ButtonData.buttonLoreStyle)
        clickAction = if (skill.isEnabledSkill(player)) {
            clickAction.append(Component.text("Disable skill").normalize(NamedTextColor.RED))
        } else {
            clickAction.append(Component.text("Enable skill").normalize(NamedTextColor.GREEN))
        }
        lore.add(clickAction)
        menuIcon = ButtonItem(Material.WRITABLE_BOOK, Component.text(skill.displayName).normalize(), lore = lore)
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        skill.toggleSkill(player, player.getJobLevel(skill.ownerJob))
        menu.refresh()
        player.getStatus().openMenu(menu, 0, true)
    }
}