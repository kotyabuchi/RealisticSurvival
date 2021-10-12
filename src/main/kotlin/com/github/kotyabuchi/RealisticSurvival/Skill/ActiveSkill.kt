package com.github.kotyabuchi.RealisticSurvival.Skill

import com.github.kotyabuchi.RealisticSurvival.Utility.floor1Digits
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

interface ActiveSkill: ToggleSkill {
    val coolTime: Long
    val hasActiveTime: Boolean
    val activeTimeMap: MutableMap<UUID, BukkitTask>
    val lastUseTime: MutableMap<UUID, Long>

    fun calcActiveTime(level: Int): Int

    override fun enableSkill(player: Player, level: Int) {
        val uuid = player.uniqueId
        if (needLevel > level) {
            player.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 2f)
            player.sendActionBar(
                Component.text("$skillName: Not enough levels (Need Lv.$needLevel)").color(NamedTextColor.RED))
        } else if (!isReadySkill(uuid)) {
            player.sendActionBar(
                Component.text("$skillName: Not yet (${(getRemainingCoolTime(uuid) / 1000.0).floor1Digits()}s)").color(
                    NamedTextColor.RED))
        } else if (!isEnabledSkill(player)) {
            enableAction(player, level)
            setSkillLevel(player, level)
            if (hasActiveTime) restartActiveTime(player, level)
        }
    }

    override fun disableSkill(player: Player) {
        val uuid = player.uniqueId
        disableAction(player)
        activeTimeMap[uuid]?.cancel()
        activeTimeMap.remove(uuid)
        removeSkillLevel(player)
    }

    fun setLastUseTime(uuid: UUID) {
        lastUseTime[uuid] = System.currentTimeMillis()
    }

    fun getRemainingCoolTime(uuid: UUID): Long {
        return lastUseTime[uuid]?.let { coolTime - (System.currentTimeMillis() - it) } ?: 0
    }

    fun isReadySkill(uuid: UUID): Boolean {
        return getRemainingCoolTime(uuid) <= 0L
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