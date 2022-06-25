package com.github.kotyabuchi.RealisticSurvival.Monster.CustomMonster

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType
import java.util.*

interface Pet {

    val ownerKey: NamespacedKey

    fun getOwner(pet: LivingEntity): LivingEntity? {
        val ownerIDString = pet.persistentDataContainer.get(ownerKey, PersistentDataType.STRING) ?: return null
        return try {
            val ownerUUID = UUID.fromString(ownerIDString)
            Bukkit.getServer().getEntity(ownerUUID) as? LivingEntity
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    fun setOwner(pet: LivingEntity, owner: LivingEntity) {
        pet.persistentDataContainer.set(ownerKey, PersistentDataType.STRING, owner.uniqueId.toString())
    }
}