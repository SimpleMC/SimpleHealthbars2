package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.simplemc.simplehealthbars2.getDisplayName
import org.simplemc.simplehealthbars2.getHealthRatio

class NameHealthbar(private val config: Config) : Healthbar {
    data class Config(
        val style: Healthbar.Style = Healthbar.Style.BAR,
        val length: Int = 10,
        val char: Char = 0x25ae.toChar(),
        val showMobNames: Boolean = true
    )

    private val step = config.char.toString()

    override fun updateHealth(target: LivingEntity): (() -> Unit)? {
        val hadCustomName = target.isCustomNameVisible
        val oldName = target.getDisplayName()

        var health = when (config.style) {
            Healthbar.Style.ABSOLUTE -> healthAmount(Math.ceil(target.health).toInt())
            Healthbar.Style.RATIO -> healthAmount(Math.ceil(target.getHealthRatio() * 100).toInt())
            Healthbar.Style.BAR -> healthBar(Math.ceil(target.getHealthRatio() * config.length).toInt())
        }

        if (config.showMobNames) {
            health = "$oldName ${ChatColor.WHITE}[$health${ChatColor.WHITE}]"
        }

        target.customName = health
        target.isCustomNameVisible = true

        return {
            if (hadCustomName) {
                target.customName = oldName
            } else {
                target.isCustomNameVisible = false
                target.customName = null
            }
        }
    }

    private fun healthBar(healthyAmount: Int): String =
        "${ChatColor.DARK_GREEN}${step.repeat(healthyAmount)}${ChatColor.DARK_RED}${step.repeat(config.length - healthyAmount)}"

    private fun healthAmount(amount: Int): String = "${ChatColor.WHITE}$amount ${ChatColor.RED}${0x2665.toChar()}"
}
