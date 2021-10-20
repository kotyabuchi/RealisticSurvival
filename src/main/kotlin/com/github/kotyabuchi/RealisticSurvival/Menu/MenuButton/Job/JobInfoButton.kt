package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job

import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.Menu.SkillInfoMenu
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.floor1Digits
import com.github.kotyabuchi.RealisticSurvival.Utility.floor2Digits
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import kotlin.math.round

class JobInfoButton(private val jobType: JobType, player: Player): MenuButton() {

    init {
        val jobStatus = player.getStatus().getJobStatus(jobType.jobClass)
        val lore = mutableListOf<Component>()
        lore.add(Component.text("Level: ${jobStatus.getLevel()}", ButtonData.buttonLoreStyle))
        val exp = jobStatus.getExp().floor2Digits()
        val nextLevel = jobStatus.getNextLevelExp()
        lore.add(Component.text("Need Exp: $exp/$nextLevel [${(round(exp / nextLevel * 1000) / 10).floor1Digits()}%]",
            ButtonData.buttonLoreStyle
        ))
        lore.add(Component.text("Total Exp: ${jobStatus.getTotalExp().floor2Digits()}", ButtonData.buttonLoreStyle))
        menuIcon = ButtonItem(jobType.getIcon(), Component.text(jobType.regularName), lore = lore)
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().openMenu(SkillInfoMenu(jobType.jobClass))
    }
}