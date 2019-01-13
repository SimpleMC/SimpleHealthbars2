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
    else -> type.name.toLowerCase().split('_').map(String::capitalize).joinToString(separator = " ")
}

fun LivingEntity.setCustomDisplayName(name: String) = when {
    this is Player -> displayName = name
    else -> {
        customName = name
        isCustomNameVisible = true
    }
}
