package org.simplemc.simplehealthbars2.healthbar

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.RenderType
import org.simplemc.simplehealthbars2.getDamagedHealthRatio
import java.time.Duration
import kotlin.math.ceil

class ScoreboardHealthbar(final override val config: Config) : PlayerHealthbar {

    companion object {
        const val OBJECTIVE_NAME = "simplehealthbar"
    }

    data class Config(
        val useMainScoreboard: Boolean = false,
        override val style: Healthbar.Style = Healthbar.Style.ABSOLUTE,
        override val duration: Duration = Duration.ofSeconds(5)
    ) : Healthbar.Config

    private val objective: Objective

    init {
        val scoreboardManager = checkNotNull(Bukkit.getScoreboardManager())

        // clean up previously added objective if it exists
        scoreboardManager.mainScoreboard.getObjective(OBJECTIVE_NAME)?.unregister()

        val scoreboard = if (config.useMainScoreboard) {
            scoreboardManager.mainScoreboard
        } else {
            scoreboardManager.newScoreboard
        }

        objective = when (config.style) {
            Healthbar.Style.ABSOLUTE -> scoreboard.registerNewObjective(
                OBJECTIVE_NAME,
                "health",
                "${ChatColor.RED}${0x2764.toChar()}",
                RenderType.HEARTS
            )
            Healthbar.Style.PERCENT -> scoreboard.registerNewObjective(
                OBJECTIVE_NAME,
                "dummy",
                "${ChatColor.RED}${0x2764.toChar()}",
                RenderType.INTEGER
            )
            Healthbar.Style.BAR ->
                throw IllegalArgumentException("BAR healthbar style not valid for scoreboard healthbar!")
        }

        objective.displaySlot = DisplaySlot.BELOW_NAME
    }

    override fun updateHealth(source: LivingEntity?, target: LivingEntity, damage: Double): (() -> Unit)? {
        if (target is Player) {
            val oldScoreboard = target.scoreboard
            target.scoreboard = checkNotNull(objective.scoreboard)

            if (config.style == Healthbar.Style.PERCENT) {
                objective.getScore(target.name).score =
                        ceil(target.getDamagedHealthRatio(damage) * 100).toInt()
            }

            return { target.scoreboard = oldScoreboard }
        }

        return null
    }
}
