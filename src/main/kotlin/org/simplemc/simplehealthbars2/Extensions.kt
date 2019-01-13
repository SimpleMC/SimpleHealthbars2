package org.simplemc.simplehealthbars2

import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

fun LivingEntity.getHealthRatio(): Double = health / getAttribute(Attribute.GENERIC_MAX_HEALTH).value

fun LivingEntity.getDisplayName(): String =
    if (isCustomNameVisible) {
        customName
    } else {
        type.name.toLowerCase().split('_').map(String::capitalize).joinToString(separator = " ")
    }
