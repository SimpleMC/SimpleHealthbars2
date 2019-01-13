package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.RenderType
import org.simplemc.simplehealthbars2.getDamagedHealth
import org.simplemc.simplehealthbars2.getDamagedHealthRatio

class ScoreboardHealthbar(private val config: Config) : PlayerHealthbar {
    data class Config(val style: Healthbar.Style = Healthbar.Style.BAR, val length: Int = 20)

    private val objective =
        Bukkit.getScoreboardManager().newScoreboard.registerNewObjective("healthbar", "dummy", "")

    init {
        objective.displaySlot = DisplaySlot.BELOW_NAME

        when (config.style) {
            Healthbar.Style.ABSOLUTE, Healthbar.Style.PERCENT -> {
                objective.displayName = "${ChatColor.RED}${0x2764.toChar()}"
                objective.renderType = RenderType.INTEGER
            }
            Healthbar.Style.BAR -> objective.renderType = RenderType.HEARTS
        }
    }

    override fun updateHealth(target: LivingEntity, damage: Double): (() -> Unit)? {
        if (target is Player) {
            val oldScoreboard = target.scoreboard
            objective.getScore(target.uniqueId.toString()).score = when (config.style) {
                Healthbar.Style.ABSOLUTE -> Math.ceil(target.getDamagedHealth(damage)).toInt()
                Healthbar.Style.PERCENT -> Math.ceil(target.getDamagedHealthRatio(damage) * 100).toInt()
                Healthbar.Style.BAR -> Math.ceil(target.getDamagedHealthRatio(damage) * config.length).toInt()
            }
            target.scoreboard = objective.scoreboard
            return { target.scoreboard = oldScoreboard }
        }

        return null
    }
}
