package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.entity.LivingEntity
import org.simplemc.simplehealthbars2.getCustomDisplayName
import org.simplemc.simplehealthbars2.setCustomDisplayName

class NameHealthbar(config: Config) : StringHealthbar(config),  MobHealthbar {
    override fun updateHealth(source: LivingEntity?, target: LivingEntity, damage: Double): (() -> Unit)? {
        val hadCustomName = target.isCustomNameVisible
        val oldName = target.getCustomDisplayName()

        target.setCustomDisplayName(formatHealthbar(target, oldName, damage))

        return {
            if (hadCustomName) {
                target.setCustomDisplayName(oldName)
            } else {
                target.isCustomNameVisible = false
                target.customName = null
            }
        }
    }
}
