package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.entity.LivingEntity
import org.simplemc.simplehealthbars2.getDisplayName

class NameHealthbar(config: Config) : StringHealthbar(config), MobHealthbar {
    override fun updateHealth(source: LivingEntity?, target: LivingEntity, damage: Double): (() -> Unit) {
        val oldCustomName = target.customName
        val wasCustomNameVisible = target.isCustomNameVisible

        target.customName = formatHealthbar(target, target.getDisplayName(), damage)
        target.isCustomNameVisible = true

        return {
            target.customName = oldCustomName
            target.isCustomNameVisible = wasCustomNameVisible
        }
    }
}
