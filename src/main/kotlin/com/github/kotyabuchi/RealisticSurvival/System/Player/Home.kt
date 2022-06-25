package com.github.kotyabuchi.RealisticSurvival.System.Player

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import java.util.*

data class Home(val homeId: Int?, val name: String, val world: World, val x: Double, val y: Double, val z: Double, val yaw: Float, var icon: Material, val creator: UUID? = null, var isShare: Boolean = false) {

    constructor(name: String, location: Location, icon: Material) : this(null, name, location.world, location.x, location.y, location.z, location.yaw, icon)

    fun changeIcon(icon: Material) {
        this.icon = icon
    }
}
