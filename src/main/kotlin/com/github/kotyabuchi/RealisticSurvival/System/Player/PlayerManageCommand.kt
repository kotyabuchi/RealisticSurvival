package com.github.kotyabuchi.RealisticSurvival.System.Player

import com.github.kotyabuchi.RealisticSurvival.Main
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PlayerManageCommand: CommandExecutor, TabCompleter, KoinComponent {

    private val main: Main by inject()

    private val args1List = listOf("mana")
    private val manaArgs2List = listOf("set", "increase", "decrease")
    private val manaArgs3List = listOf("mana", "maxmana")

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val result = mutableListOf<String>()
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
                }
            }
            3 -> {
                when (args[0].lowercase()) {
                    "mana" -> {
                        manaArgs3List.forEach {
                            if (it.contains(args[2], true)) result.add(it)
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
                }
            }
        }
        return result
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return true
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
        }
        return true
    }
}