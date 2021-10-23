package com.github.kotyabuchi.RealisticSurvival.Skill

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.floor1Digits
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import java.util.*

interface Skill: Listener, KoinComponent {

    val main: Main

    val skillName: String
    val cost: Int
    val needLevel: Int
    val description: String
    val coolTime: Long
    val lastUseTime: MutableMap<UUID, Long>

    fun getSkillNamespacedKey(): NamespacedKey = NamespacedKey(main, skillName)

    fun enableSkill(player: Player, level: Int) {
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
            }
            else -> {
                sendErrorMessage(player, "$skillName: Not enough mana (Need Lv.$cost)")
            }
        }
    }

    fun enableAction(player: Player, level: Int) {
        player.playSound(player.eyeLocation, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 2.0f)
        player.sendActionBar(Component.text(skillName, NamedTextColor.GREEN))
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

    fun sendErrorMessage(player: Player, message: String) {
        player.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 2f)
        player.sendActionBar(Component.text(message).color(NamedTextColor.RED))
    }
}