package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.entity.LivingEntity
import java.time.Duration

interface Healthbar {
    enum class Type { NAME, SCOREBOARD, ACTION, NONE }
    enum class Style { ABSOLUTE, PERCENT, BAR }

    interface Config {
        val style: Style
        val duration: Duration
    }

    val config: Config
    val durationTicks: Long
        get() = config.duration.seconds * 20

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
