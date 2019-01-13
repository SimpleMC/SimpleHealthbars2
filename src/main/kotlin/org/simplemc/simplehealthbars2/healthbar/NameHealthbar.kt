package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.simplemc.simplehealthbars2.getCustomDisplayName
import org.simplemc.simplehealthbars2.getDamagedHealth
import org.simplemc.simplehealthbars2.getDamagedHealthRatio
import org.simplemc.simplehealthbars2.setCustomDisplayName
import java.util.logging.Logger

class NameHealthbar(private val config: Config) : MobHealthbar {
    data class Config(
        val style: Healthbar.Style = Healthbar.Style.BAR,
        val length: Int = 20,
        val char: Char = 0x25ae.toChar(),
        val showMobNames: Boolean = true
    )

    private val step = config.char.toString()

    override fun updateHealth(target: LivingEntity, damage: Double): (() -> Unit)? {
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

    private fun formatHealthbar(target: LivingEntity, oldName: String, damage: Double): String {
        var health = when (config.style) {
            Healthbar.Style.ABSOLUTE -> healthAmount(Math.ceil(target.getDamagedHealth(damage)).toInt())
            Healthbar.Style.PERCENT -> healthAmount(Math.ceil(target.getDamagedHealthRatio(damage) * 100).toInt())
            Healthbar.Style.BAR -> healthBar(Math.ceil(target.getDamagedHealthRatio(damage) * config.length).toInt())
        }

        if (target.isCustomNameVisible || config.showMobNames) {
            health = "$oldName $health"
        }

        return health
    }

    private fun healthBar(healthyAmount: Int): String {
        val health = step.repeat(healthyAmount)
        val missing = step.repeat(config.length - healthyAmount)
        return "${ChatColor.WHITE}[${ChatColor.DARK_GREEN}$health${ChatColor.DARK_RED}$missing${ChatColor.WHITE}]"
    }

    private fun healthAmount(amount: Int): String = "${ChatColor.WHITE}$amount ${ChatColor.RED}${0x2764.toChar()}"
}
