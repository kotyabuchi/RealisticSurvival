package com.github.kotyabuchi.RealisticSurvival.Monster.CustomMonster

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType
import java.util.*

interface PetOwner {

    val maxPetAmount: Int
    val petKey: NamespacedKey

    fun getPets(owner: LivingEntity): List<UUID> {
        val pets = owner.persistentDataContainer.get(petKey, PersistentDataType.STRING)?.split(",") ?: mutableListOf()
        val result = mutableListOf<UUID>()
        pets.forEach {
            try {
                val uuid = UUID.fromString(it)
                Bukkit.getServer().getEntity(uuid)?.let { pet ->
                    if (!pet.isDead) result.add(uuid)
                }
            } catch (_: IllegalArgumentException) {}
        }
        return result
    }

    fun getPetAmount(owner: LivingEntity): Int {
        return getPets(owner).size
    }

    fun canSummonPet(owner: LivingEntity): Boolean {
        return getPetAmount(owner) < maxPetAmount
    }

    fun setPetsUUID(owner: LivingEntity, pets: List<UUID>) {
        var petsString = ""
        pets.forEach {
            petsString += "$it,"
        }
        petsString = petsString.dropLast(1)
        owner.persistentDataContainer.set(petKey, PersistentDataType.STRING, petsString)
    }

    fun addPet(owner: LivingEntity, vararg entities: LivingEntity) {
        val pets = getPets(owner).toMutableList()
        entities.forEach { pet ->
            pets.add(pet.uniqueId)
        }
        setPetsUUID(owner, pets)
    }

    fun removePet(owner: LivingEntity, pet: LivingEntity): Boolean {
        val pets = getPets(owner).toMutableList()
        val result = pets.removeFirstOrNull()
        setPetsUUID(owner, pets)
        return result != null
    }
}