package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.entity.LivingEntity

interface Healthbar {
    enum class Type { NAME, SCOREBOARD, NONE }
    enum class Style { ABSOLUTE, PERCENT, BAR }

    /**
     * Update target's healthbar with latest health
     *
     * @param target healthbar target
     * @param damage damage to apply (subtracts from current health)
     *
     * @return function to remove healthbar from target
     */
    fun updateHealth(target: LivingEntity, damage: Double): (() -> Unit)?
}

interface PlayerHealthbar : Healthbar

interface MobHealthbar : Healthbar
