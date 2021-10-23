package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.Skill.Skill
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material

class SkillInfoButton(skill: Skill): MenuButton() {

    init {
        val lore = mutableListOf<Component>()
        lore.add(Component.text("NeedLevel: ", ButtonData.buttonLoreStyle).append(Component.text(skill.needLevel).normalize()))
        lore.add(Component.text("Cost: ", ButtonData.buttonLoreStyle).append(Component.text(skill.cost).normalize(NamedTextColor.AQUA)))
        if (skill.coolTime != 0L) lore.add(Component.text("CoolTime: ", ButtonData.buttonLoreStyle).append(Component.text("${skill.coolTime / 1000}s").normalize()))
        lore.add(Component.text("===============", ButtonData.buttonLoreStyle))
        lore.add(Component.text(skill.description, ButtonData.buttonLoreStyle))
        menuIcon = ButtonItem(Material.WRITABLE_BOOK, Component.text(skill.skillName).normalize(), lore = lore)
        clickSound = null
    }
}