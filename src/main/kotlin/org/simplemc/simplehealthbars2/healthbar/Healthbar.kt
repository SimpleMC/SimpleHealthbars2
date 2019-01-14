package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.entity.LivingEntity

interface Healthbar {
    enum class Type { NAME, SCOREBOARD, ACTION, NONE }
    enum class Style { ABSOLUTE, PERCENT, BAR }

    /**
     * Update target's healthbar with latest health
     *
     * @param source healthbar source
     * @param target healthbar target
     * @param damage damage to apply (subtracts from current health)
     *
     * @return function to remove healthbar from target
     */
    fun updateHealth(source: LivingEntity?, target: LivingEntity, damage: Double): (() -> Unit)?
}

interface PlayerHealthbar : Healthbar

interface MobHealthbar : Healthbar
