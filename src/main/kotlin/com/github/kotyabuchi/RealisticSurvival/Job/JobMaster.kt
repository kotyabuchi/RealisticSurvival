package com.github.kotyabuchi.RealisticSurvival.Job

import com.github.kotyabuchi.RealisticSurvival.Event.PlayerInteractBlockEvent
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.SkillCommand
import com.github.kotyabuchi.RealisticSurvival.Skill.ToggleSkill
import com.github.kotyabuchi.RealisticSurvival.System.Player.getJobLevel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration

open class JobMaster(val jobName: String): Listener, KoinComponent {
    
    private val main: Main by inject()

    fun getExpBossBarKey(player: Player): NamespacedKey = NamespacedKey(main, this.jobName + "_ExpBar_" + player.uniqueId.toString())

    private val targetTool: MutableList<Material> = mutableListOf()
    private val castingModeList: MutableList<Player> = mutableListOf()
    private val castingCommandMap: MutableMap<Player, String> = mutableMapOf()
    private val skillMap: MutableMap<SkillCommand, ToggleSkill> = mutableMapOf()

    private val commandTitleTime = Title.Times.of(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)

    @EventHandler
    fun modeChange(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        if (player.isSneaking) return
        if (!targetTool.contains(event.offHandItem?.type)) return
        event.isCancelled = true
        if (castingModeList.contains(player)) {
            activeSkill(player)
            castingModeList.remove(player)
            castingCommandMap.remove(player)
        } else {
            castingModeList.add(player)
            castingCommandMap[player] = ""
            player.sendActionBar(Component.text("Cast Mode Enabled", NamedTextColor.GREEN))
            player.showTitle(Title.title(Component.empty(), Component.text("- - -"), commandTitleTime))
            player.world.playSound(player.eyeLocation, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 2.0f)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onClick(event: PlayerInteractEvent) {
        if (event is PlayerInteractBlockEvent) return
        val player = event.player
        if (!castingModeList.contains(player)) return
        event.isCancelled = true
        if (event.hand != EquipmentSlot.HAND) return
        val action = event.action
        if (action == Action.PHYSICAL) return
        castingCommandMap[player]?.let {
            val thisTimeAction = if (action.name.startsWith("LEFT_CLICK")) {
                player.playSound(player.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.2f, 1.3f)
                "L"
            } else {
                player.playSound(player.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.2f, 0.7f)
                "R"
            }
            val newActionStr = it + thisTimeAction
            castingCommandMap[player] = newActionStr
            var subTitle = Component.text("")
            val actionLength = newActionStr.length

            repeat(3) { repeatTime ->
                subTitle = if (actionLength > repeatTime) {
                    val actionChar = newActionStr[repeatTime]
                    subTitle.append(Component.text(actionChar, if (actionChar == 'L') NamedTextColor.GREEN else NamedTextColor.RED))
                } else {
                    subTitle.append(Component.text("-"))
                }
                if (repeatTime < 2) {
                    subTitle = subTitle.append(Component.text(" "))
                }
            }

            player.showTitle(Title.title(Component.empty(), subTitle, commandTitleTime))
            if (actionLength == 3) {
                activeSkill(player)
                castingModeList.remove(player)
                castingCommandMap.remove(player)
            }
        }
    }

    @EventHandler
    fun onClick(event: PlayerItemHeldEvent) {
        val player = event.player
        if (!castingModeList.contains(player)) return
        castingModeList.remove(player)
        castingCommandMap.remove(player)
        player.playSound(player.eyeLocation, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 2f)
        player.sendActionBar(Component.text("Cast Mode canceled", NamedTextColor.RED))
    }

    fun getTool(): List<Material> {
        return targetTool
    }

    protected fun addTool(tool: Material): JobMaster {
        targetTool.add(tool)
        return this
    }

    fun isJobTool(tool: Material): Boolean {
        return targetTool.contains(tool)
    }

    protected fun registerSkill(skillCommand: SkillCommand, skill: ToggleSkill) {
        skillMap[skillCommand] = skill
    }

    fun getSkills(): Map<SkillCommand, ToggleSkill> {
        return skillMap
    }

    private fun activeSkill(player: Player) {
        val castingAction = castingCommandMap[player] ?: return
        try {
            val skillCommand = SkillCommand.valueOf(castingAction)
            val skill = skillMap[skillCommand]
            if (skill == null) {
                notRegisterActionNotice(player)
            } else {
                skill.toggleSkill(player, player.getJobLevel(this))
            }
        } catch (e: IllegalArgumentException) {
            notRegisterActionNotice(player)
        }
    }

    private fun notRegisterActionNotice(player: Player) {
        player.playSound(player.eyeLocation, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 2f)
        player.sendActionBar(Component.text("Not registered skill", NamedTextColor.RED))
    }

    open fun levelUpEvent(player: Player) {}
}