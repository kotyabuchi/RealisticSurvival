package com.github.kotyabuchi.RealisticSurvival.Monster.CustomMonster

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.ItemUtil
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Vex
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object CursedEye: CustomEntity(), Pet, KoinComponent {

    private val main: Main by inject()

    override val nameKey = NamespacedKey(main, "CURSED_EYE")
    override val damageSound: Sound = Sound.sound(org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH.key(), Sound.Source.AMBIENT, 1f, .5f)
    override val ownerKey: NamespacedKey = NamespacedKey(main, "OWNER")

    init {
        main.registerEvent(this)
    }

    override fun spawn(owner: LivingEntity): LivingEntity? {
        if (owner !is Mob) return null
        val eye = owner.world.spawn(owner.location.add(.0, 1.0, .0), Vex::class.java) { eye ->
            eye.isSilent = true
            eye.customName(Component.text("Cursed Eye"))
            eye.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
            eye.addPotionEffect(PotionEffect(PotionEffectType.SLOW, Int.MAX_VALUE, 1, false, false))
            eye.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 1.0
            eye.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 1.0
            eye.health = 1.0
            eye.persistentDataContainer.run {
                set(nameKey, PersistentDataType.BYTE, 1)
            }

            eye.equipment.run {
                helmetDropChance = 0f
                helmet = ItemUtil.createSkull("Cursed Eye", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFhOGZjOGRlNjQxN2I0OGQ0OGM4MGI0NDNjZjUzMjZlM2Q5ZGE0ZGJlOWIyNWZjZDQ5NTQ5ZDk2MTY4ZmMwIn19fQ==")
                setItemInMainHand(null)
            }
        }
        setOwner(eye, owner)
        eye.target = owner.target
        return eye
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val damager = event.damager as? Vex ?:return
        if (damager.persistentDataContainer.has(nameKey, PersistentDataType.BYTE)) {
            event.isCancelled = true
            entity.damage(1.0)
            entity.noDamageTicks = 0
        }
    }

    @EventHandler
    fun onDeath(event: EntityDeathEvent) {
        val entity = event.entity as? LivingEntity ?: return
        if (!entity.persistentDataContainer.has(nameKey, PersistentDataType.BYTE)) return
        val owner = getOwner(entity) ?: return

    }
}