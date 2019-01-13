package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.RenderType
import org.simplemc.simplehealthbars2.getHealthRatio

class ScoreboardHealthbar(private val config: Config) : Healthbar {
    data class Config(val style: Healthbar.Style, val length: Int = 10)

    private val objective =
        Bukkit.getScoreboardManager().newScoreboard.registerNewObjective("healthbar", "dummy", "Healthbar")

    init {
        objective.displaySlot = DisplaySlot.BELOW_NAME

        when (config.style) {
            Healthbar.Style.ABSOLUTE, Healthbar.Style.RATIO -> {
                objective.displayName = " ${ChatColor.RED}${0x2665.toChar()}"
                objective.renderType = RenderType.INTEGER
            }
            Healthbar.Style.BAR -> objective.renderType = RenderType.HEARTS
        }
    }

    override fun updateHealth(target: LivingEntity): (() -> Unit)? {
        if (target is Player) {
            val oldScoreboard = target.scoreboard
            objective.getScore(target.uniqueId.toString()).score = when (config.style) {
                Healthbar.Style.ABSOLUTE -> Math.ceil(target.health).toInt()
                Healthbar.Style.RATIO -> Math.ceil(target.getHealthRatio() * 100).toInt()
                Healthbar.Style.BAR -> Math.ceil(target.getHealthRatio() * config.length).toInt()
            }
            target.scoreboard = objective.scoreboard
            return { target.scoreboard = oldScoreboard }
        }

        return null
    }
}
