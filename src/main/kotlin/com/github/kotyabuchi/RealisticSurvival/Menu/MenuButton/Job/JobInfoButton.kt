package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job

import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.Menu.SkillInfoMenu
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.floor1Digits
import com.github.kotyabuchi.RealisticSurvival.Utility.floor2Digits
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import kotlin.math.round

class JobInfoButton(private val jobType: JobType, player: Player): MenuButton() {

    init {
        val jobStatus = player.getStatus().getJobStatus(jobType.jobClass)
        val exp = jobStatus.getExp().floor2Digits()
        val nextLevel = jobStatus.getNextLevelExp()
        val lore = mutableListOf<Component>()
        lore.add(Component.text("Level: ", ButtonData.buttonLoreStyle).append(Component.text("${jobStatus.getLevel()}").normalize()))
        lore.add(Component.text("Need Exp: ",ButtonData.buttonLoreStyle)
            .append(Component.text("$exp/$nextLevel [${(round(exp / nextLevel * 1000) / 10).floor1Digits()}%]").normalize()))
        lore.add(Component.text("Total Exp: ", ButtonData.buttonLoreStyle).append(Component.text("${jobStatus.getTotalExp().floor2Digits()}").normalize()))
        lore.add(Component.empty())
        lore.add(Component.text("Left Click: ", ButtonData.buttonLoreStyle).append(Component.text("Show active skill").normalize()))
        lore.add(Component.text("Right Click: ", ButtonData.buttonLoreStyle).append(Component.text("Show passive skill").normalize()))
        menuIcon = ButtonItem(jobType.getIcon(), Component.text(jobType.regularName), lore = lore)
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().openMenu(SkillInfoMenu(player, jobType.jobClass, event.isRightClick))
    }
}