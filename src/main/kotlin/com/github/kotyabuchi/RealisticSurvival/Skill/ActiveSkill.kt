package com.github.kotyabuchi.RealisticSurvival.Skill

import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.floor1Digits
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

interface ActiveSkill: ToggleSkill {
    val hasActiveTime: Boolean
    val activeTimeMap: MutableMap<UUID, BukkitTask>

    fun calcActiveTime(level: Int): Int = 0

    override fun enableSkill(player: Player, level: Int) {
        val playerStatus = player.getStatus()
        val uuid = player.uniqueId
        when {
            level < needLevel -> {
                player.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 2f)
                sendErrorMessage(player, "$skillName: Not enough levels (Need Lv.$needLevel)")
            }
            !isReadySkill(uuid) -> {
                player.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 2f)
                sendErrorMessage(player, "$skillName: Not yet (${(getRemainingCoolTime(uuid) / 1000.0).floor1Digits()}s)")
            }
            playerStatus.decreaseMana(cost) -> {
                enableAction(player, level)
                setLastUseTime(uuid)
                setSkillLevel(player, level)
                if (hasActiveTime) restartActiveTime(player, level)
            }
            else -> {
                sendErrorMessage(player, "$skillName: Not enough mana (Need Lv.$cost)")
            }
        }
    }

    override fun disableSkill(player: Player) {
        val uuid = player.uniqueId
        disableAction(player)
        activeTimeMap[uuid]?.cancel()
        activeTimeMap.remove(uuid)
        removeSkillLevel(player)
    }

    fun restartActiveTime(player: Player, level: Int = getSkillLevel(player) ?: 1) {
        val uuid = player.uniqueId
        activeTimeMap[uuid]?.cancel()
        activeTimeMap[uuid] = object : BukkitRunnable() {
            override fun run() {
                disableSkill(player)
            }
        }.runTaskLater(main, calcActiveTime(level).toLong())
    }
}