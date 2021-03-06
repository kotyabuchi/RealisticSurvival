package com.github.kotyabuchi.RealisticSurvival

import com.github.kotyabuchi.RealisticSurvival.Utility.saveFile
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

object Debug: Listener, KoinComponent {

    private val main: Main by inject()

    fun checkCanNotItemMaterials() {
        println("Start check can not item materials")
        val inv = Bukkit.createInventory(null, 6 * 9)
        val materials = Material.values().toList().chunked(6 * 9)
        val result = StringBuilder()
        object : BukkitRunnable() {
            var count = 0
            override fun run() {
                println("$count/${materials.size}")
                if (count > 0) {
                    inv.storageContents.forEachIndexed { index, itemStack ->
                        if (itemStack == null) {
                            if (materials[count - 1].size > index) result.appendLine("this == Material.${materials[count - 1][index].name} ||")
                        }
                    }
                }
                if (count >= materials.size) {
                    cancel()
                    val file = File(main.dataFolder, "CanNotItemMaterials.txt")
                    saveFile(file, result.toString())
                    println("===== Finished =====")
                } else {
                    materials[count].forEachIndexed { index, material ->
                        inv.setItem(index, ItemStack(material))
                    }
                }
                count++
            }
        }.runTaskTimer(main, 0, 1)
    }
}