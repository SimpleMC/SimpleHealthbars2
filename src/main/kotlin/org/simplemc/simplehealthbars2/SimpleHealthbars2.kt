package org.simplemc.simplehealthbars2

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.java.JavaPlugin
import org.simplemc.simplehealthbars2.healthbar.ActionHealthbar
import org.simplemc.simplehealthbars2.healthbar.Healthbar
import org.simplemc.simplehealthbars2.healthbar.MobHealthbar
import org.simplemc.simplehealthbars2.healthbar.NameHealthbar
import org.simplemc.simplehealthbars2.healthbar.PlayerHealthbar
import org.simplemc.simplehealthbars2.healthbar.ScoreboardHealthbar
import org.simplemc.simplehealthbars2.healthbar.ScoreboardHealthbar.Companion.OBJECTIVE_NAME
import org.simplemc.simplehealthbars2.healthbar.StringHealthbar
import java.time.Duration

/**
 * SimpleHealthbars2 plugin
 */
class SimpleHealthbars2 : JavaPlugin() {
    private lateinit var listener: DamageListener

    override fun onEnable() {
        // ensure config file exists
        saveDefaultConfig()

        val playerHealthbars = mutableMapOf<String?, PlayerHealthbar>()
        loadBar(config.getConfigurationSection("player-bar"))?.let { bar ->
            playerHealthbars[null] = checkNotNull(bar as? PlayerHealthbar) {
                "Invalid player healthbar type! Must be one of: SCOREBOARD, ACTION"
            }
        }

        val mobHealthbars = mutableMapOf<String?, MobHealthbar>()
        loadBar(config.getConfigurationSection("mob-bar"))?.let { bar ->
            mobHealthbars[null] = checkNotNull(bar as? MobHealthbar) {
                "Invalid mob healthbar type! Must be one of: NAME, ACTION"
            }
        }

        config.getConfigurationSection("worlds")?.let { worlds ->
            worlds.getKeys(false).forEach { worldName ->
                val worldConfig = worlds.getConfigurationSection(worldName)
                val playerBar = loadBar(worldConfig?.getConfigurationSection("player-bar"))
                val mobBar = loadBar(worldConfig?.getConfigurationSection("mob-bar"))

                playerBar?.also { bar ->
                    playerHealthbars[worldName] = checkNotNull(bar as? PlayerHealthbar) {
                        "Invalid player healthbar type! Must be one of: SCOREBOARD, ACTION"
                    }
                }

                mobBar?.also { bar ->
                    mobHealthbars[worldName] = checkNotNull(bar as? MobHealthbar) {
                        "Invalid mob healthbar type! Must be one of: NAME, ACTION"
                    }
                }
            }
        }

        logger.fine {
            """
                Loaded Healthbar configs:
                Player bars:
                ${barsConfigToString(playerHealthbars)}
                
                Mob bars:
                ${barsConfigToString(mobHealthbars)}
                
            """.trimIndent()
        }

        listener = DamageListener(this, playerHealthbars, mobHealthbars)
        server.pluginManager.registerEvents(listener, this)

        logger.info("${description.name} version ${description.version} enabled!")
    }

    private fun barsConfigToString(bars: Map<String?, Healthbar>) =
        bars.entries.joinToString(separator = "\n") { "World: ${it.key}, Config: ${it.value.config}" }.prependIndent()

    private fun loadBar(config: ConfigurationSection?) =
        config?.let {
            when (Healthbar.Type.valueOf(checkNotNull(config.getString("type")))) {
                Healthbar.Type.NAME -> NameHealthbar(loadStringBar(config))
                Healthbar.Type.ACTION -> ActionHealthbar(loadStringBar(config))
                Healthbar.Type.SCOREBOARD -> ScoreboardHealthbar(
                    ScoreboardHealthbar.Config(
                        useMainScoreboard = config.getBoolean("useMainScoreboard", false),
                        style = Healthbar.Style.valueOf(checkNotNull(config.getString("style", "ABSOLUTE"))),
                        duration = Duration.ofSeconds(config.getLong("duration", 5)),
                    ),
                )
                Healthbar.Type.NONE -> null
            }
        }

    private fun loadStringBar(config: ConfigurationSection) = StringHealthbar.Config(
        style = Healthbar.Style.valueOf(checkNotNull(config.getString("style", "BAR"))),
        duration = Duration.ofSeconds(config.getLong("duration", 5)),
        length = config.getInt("length", 20),
        char = config.getInt("char", 0x25ae).toChar(),
        showMobNames = config.getBoolean("showMobNames", true),
    )

    override fun onDisable() {
        listener.close()
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective(OBJECTIVE_NAME)?.unregister()
        logger.info("${description.name} disabled.")
    }
}
