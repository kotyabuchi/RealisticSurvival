package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton

import com.github.kotyabuchi.RealisticSurvival.Skill.ToggleSkill
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material

class SkillInfoButton(skill: ToggleSkill): MenuButton() {

    init {
        val lore = mutableListOf<Component>()
        lore.add(Component.text("NeedLevel: ", ButtonData.buttonLoreStyle).append(Component.text(skill.needLevel).normalize()))
        lore.add(Component.text("Cost: ", ButtonData.buttonLoreStyle).append(Component.text(skill.cost).normalize(NamedTextColor.AQUA)))
        lore.add(Component.text(skill.description, ButtonData.buttonLoreStyle))
        menuIcon = ButtonItem(Material.WRITABLE_BOOK, Component.text(skill.skillName).normalize(), lore = lore)
        clickSound = null
    }
}