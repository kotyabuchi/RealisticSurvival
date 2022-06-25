package com.github.kotyabuchi.RealisticSurvival.System.Player

import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.System.TombStone
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import kotlin.math.round

object PlayerManageCommand: CommandExecutor, TabCompleter, KoinComponent {

    private val main: Main by inject()
    private val server = main.server

    private val args1List = listOf("mana", "skill", "tombstone")
    private val manaArgs2List = listOf("set", "increase", "decrease")
    private val manaArgs3List = listOf("mana", "maxmana")
    private val skillArgs2List = listOf("set", "increase", "decrease")
    private val skillArgs3List = listOf("xp", "level")
    private val tombStoneArgs2List = listOf("show", "remove")

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val result = mutableListOf<String>()
        if (!sender.isOp) return result
        when (args.size) {
            1 -> {
                args1List.forEach {
                    if (it.contains(args[0], true)) result.add(it)
                }
            }
            2 -> {
                when (args[0].lowercase()) {
                    "mana" -> {
                        manaArgs2List.forEach {
                            if (it.contains(args[1], true)) result.add(it)
                        }
                    }
                    "skill" -> {
                        skillArgs2List.forEach {
                            if (it.contains(args[1], true)) result.add(it)
                        }
                    }
                    "tombstone" -> {
                        tombStoneArgs2List.forEach {
                            if (it.contains(args[1], true)) result.add(it)
                        }
                    }
                }
            }
            3 -> {
                when (args[0].lowercase()) {
                    "mana" -> {
                        manaArgs3List.forEach {
                            if (it.contains(args[2], true)) result.add(it)
                        }
                    }
                    "skill" -> {
                        skillArgs3List.forEach {
                            if (it.contains(args[2], true)) result.add(it)
                        }
                    }
                    "tombstone" -> {
                        server.onlinePlayers.forEach {
                            if (it.name.contains(args[2], true)) result.add(it.name)
                        }
                    }
                }
            }
            4 -> {
                when (args[0].lowercase()) {
                    "tombstone" -> {
                        val target = server.getPlayer(args[2])
                        target?.let { player ->
                            TombStone.getPlayerTombStones(player)?.map { tombStone -> tombStone.name }?.forEach {
                                if (it.contains(args[3], true)) result.add(it)
                            }
                        }
                    }
                }
            }
            5 -> {
                when (args[0].lowercase()) {
                    "mana" -> {
                        main.server.onlinePlayers.forEach {
                            result.add(it.name)
                        }
                    }
                    "skill" -> {
                        JobType.values().forEach {
                            if (it.name.contains(args[4], true)) result.add(it.regularName)
                        }
                    }
                }
            }
            6 -> {
                when (args[0].lowercase()) {
                    "skill" -> {
                        main.server.onlinePlayers.forEach {
                            result.add(it.name)
                        }
                    }
                }
            }
        }
        return result
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return true
        if (!sender.isOp) return true
        when (args[0].lowercase()) {
            "mana" -> {
                if (args.size < 4) return true
                val type = args[2]
                val amount = args[3].toDoubleOrNull() ?: return true
                val target = if (args.size < 5) {
                    sender as? Player ?: return true
                } else {
                    main.server.getPlayer(args[4]) ?: return true
                }
                val playerStatus = target.getStatus()
                when (args[1].lowercase()) {
                    "set" -> {
                        when (type.lowercase()) {
                            "mana" -> {
                                playerStatus.mana = amount
                            }
                            "maxmana" -> {
                                playerStatus.maxMana = amount
                            }
                        }
                    }
                    "increase" -> {
                        when (type.lowercase()) {
                            "mana" -> {
                                playerStatus.increaseMana(amount)
                            }
                            "maxmana" -> {
                                playerStatus.increaseMaxMana(amount)
                            }
                        }
                    }
                    "decrease" -> {
                        when (type.lowercase()) {
                            "mana" -> {
                                playerStatus.decreaseMana(amount)
                            }
                            "maxmana" -> {
                                playerStatus.maxMana -= amount
                            }
                        }
                    }
                }
                playerStatus.refreshManaIndicator()
            }
            "skill" -> {
                if (args.size < 5) return true
                val type = args[2]
                val doubleAmount = args[3].toDoubleOrNull() ?: return true
                val intAmount = round(doubleAmount).toInt()
                val target = if (args.size < 6) {
                    sender as? Player ?: return true
                } else {
                    main.server.getPlayer(args[5]) ?: return true
                }
                val targetJob = JobType.valueOf(args[4].uppercase()).jobClass
                val playerStatus = target.getStatus()
                val jobStatus = playerStatus.getJobStatus(targetJob)
                jobStatus.resetCombo()
                when (args[1].lowercase()) {
                    "set" -> {
                        when (type.lowercase()) {
                            "xp" -> {
                                jobStatus.setExp(doubleAmount, true)
                            }
                            "level" -> {
                                jobStatus.setLevel(intAmount)
                            }
                        }
                    }
                    "increase" -> {
                        when (type.lowercase()) {
                            "xp" -> {
                                jobStatus.addExp(doubleAmount, 0)
                            }
                            "level" -> {
                                jobStatus.addLevel(intAmount)
                            }
                        }
                    }
                    "decrease" -> {
                        when (type.lowercase()) {
                            "xp" -> {
                                jobStatus.setTotalExp(jobStatus.getTotalExp() - doubleAmount)
                            }
                            "level" -> {
                                jobStatus.setLevel(jobStatus.getLevel() - intAmount)
                            }
                        }
                    }
                }
            }
            "tombstone" -> {
                if (args.size < 3) return true
                val target = server.getPlayer(args[2])

                if (target == null)  {
                    sender.sendMessage("[${args[2]}]が見つかりません")
                    return true
                }

                val playerTombStones = TombStone.getPlayerTombStones(target)

                when (args.size) {
                    3 -> {
                        if (args[1].lowercase() == "show") {
                            sender.sendMessage(Component.text("======================").normalize(NamedTextColor.GREEN))
                            playerTombStones?.forEach {
                                sender.sendMessage(it.name)
                            }
                            sender.sendMessage(Component.text("======================").normalize(NamedTextColor.GREEN))
                        }
                    }
                    4 -> {
                        try {
                            val tombStoneUUID = UUID.fromString(args[3])
                            val tombStoneItem = TombStone.getTombStoneItems(target, tombStoneUUID)

                            when (args[1]) {
                                "show" -> {
                                    if (sender is Player) {
                                        tombStoneItem?.let {
                                            val viewer = main.server.createInventory(null, 9 * 5)
                                            for ((index, slot) in EquipmentSlot.values().withIndex()) {
                                                tombStoneItem.getEquipmentItems()[slot]?.let { item ->
                                                    viewer.setItem(index, item)
                                                }
                                            }
                                            for (index in 0 until (9 * 4)) {
                                                tombStoneItem.getStorageItems()[index]?.let { item ->
                                                    viewer.setItem(index + 9, item)
                                                }
                                            }
                                            sender.openInventory(viewer)
                                        }
                                    } else {
                                        sender.sendMessage("プレイヤー限定コマンドです")
                                    }
                                }
                                "remove" -> {
                                    for (world in server.worlds) {
                                        val tombStone = world.getEntity(tombStoneUUID) as? ArmorStand ?: continue
                                        TombStone.removeTombStone(target, tombStone)
                                        sender.sendMessage("墓石[${args[3]}]を削除しました")
                                    }
                                }
                            }
                        } catch (e: IllegalArgumentException) {
                            sender.sendMessage("不正な墓石IDです")
                        }
                    }
                }
            }
        }
        return true
    }
}