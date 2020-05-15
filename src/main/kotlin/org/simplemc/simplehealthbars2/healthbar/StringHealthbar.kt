package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.simplemc.simplehealthbars2.getDamagedHealth
import org.simplemc.simplehealthbars2.getDamagedHealthRatio
import java.time.Duration

abstract class StringHealthbar(final override val config: Config) : Healthbar {
    data class Config(
        override val style: Healthbar.Style = Healthbar.Style.BAR,
        override val duration: Duration = Duration.ofSeconds(5),
        val length: Int = 20,
        val char: Char = 0x25ae.toChar(),
        val showMobNames: Boolean = true
    ) : Healthbar.Config

    private val step = config.char.toString()

    internal fun formatHealthbar(target: LivingEntity, oldName: String, damage: Double): String {
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
