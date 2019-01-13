package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.RenderType
import org.simplemc.simplehealthbars2.getDamagedHealthRatio

class ScoreboardHealthbar(private val config: Config) : PlayerHealthbar {
    data class Config(val style: Healthbar.Style = Healthbar.Style.ABSOLUTE)

    private val objective: Objective

    init {
        objective = when (config.style) {
            Healthbar.Style.ABSOLUTE -> Bukkit.getScoreboardManager().newScoreboard.registerNewObjective(
                "healthbar",
                "health",
                "",
                RenderType.HEARTS
            )
            Healthbar.Style.PERCENT -> Bukkit.getScoreboardManager().newScoreboard.registerNewObjective(
                "healthbar",
                "dummy",
                "${ChatColor.RED}${0x2764.toChar()}",
                RenderType.INTEGER
            )
            Healthbar.Style.BAR ->
                throw IllegalArgumentException("BAR healthbar style not valid for scoreboard healthbar!")
        }

        objective.displaySlot = DisplaySlot.BELOW_NAME
    }

    override fun updateHealth(target: LivingEntity, damage: Double): (() -> Unit)? {
        if (target is Player) {
            val oldScoreboard = target.scoreboard
            target.scoreboard = objective.scoreboard

            if (config.style == Healthbar.Style.PERCENT) {
                objective.getScore(target.uniqueId.toString()).score =
                        Math.ceil(target.getDamagedHealthRatio(damage) * 100).toInt()
            }

            return { target.scoreboard = oldScoreboard }
        }

        return null
    }
}
