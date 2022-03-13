package org.simplemc.simplehealthbars2

import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import kotlin.math.max

fun LivingEntity.getDamagedHealth(damage: Double): Double = max(health - damage, 0.0)

fun LivingEntity.getDamagedHealthRatio(damage: Double): Double =
    getDamagedHealth(damage) / checkNotNull(getAttribute(Attribute.GENERIC_MAX_HEALTH)).value

fun LivingEntity.getCustomDisplayName(): String = when {
    this is Player -> displayName
    isCustomNameVisible -> checkNotNull(customName)
    else -> type.name.lowercase().split('_').joinToString(separator = " ", transform = String::titlecaseFirstChar)
}

fun String.titlecaseFirstChar() = replaceFirstChar(Char::titlecase)

fun LivingEntity.setCustomDisplayName(name: String) {
    customName = name
    isCustomNameVisible = true
}
