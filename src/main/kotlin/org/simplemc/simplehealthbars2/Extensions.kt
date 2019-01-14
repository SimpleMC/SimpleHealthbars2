package org.simplemc.simplehealthbars2

import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

fun LivingEntity.getDamagedHealth(damage: Double): Double = Math.max(health - damage, 0.0)

fun LivingEntity.getDamagedHealthRatio(damage: Double): Double =
    getDamagedHealth(damage) / getAttribute(Attribute.GENERIC_MAX_HEALTH).value

fun LivingEntity.getCustomDisplayName(): String = when {
    this is Player -> displayName
    isCustomNameVisible -> customName
    else -> type.name.toLowerCase().split('_').joinToString(separator = " ", transform = String::capitalize)
}

fun LivingEntity.setCustomDisplayName(name: String) {
    customName = name
    isCustomNameVisible = true
}
