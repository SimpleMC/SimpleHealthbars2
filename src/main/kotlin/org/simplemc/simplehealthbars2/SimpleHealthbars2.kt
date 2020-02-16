package org.simplemc.simplehealthbars2

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.java.JavaPlugin
import org.simplemc.simplehealthbars2.healthbar.ActionHealthbar
import org.simplemc.simplehealthbars2.healthbar.Healthbar
import org.simplemc.simplehealthbars2.healthbar.MobHealthbar
import org.simplemc.simplehealthbars2.healthbar.NameHealthbar
import org.simplemc.simplehealthbars2.healthbar.PlayerHealthbar
import org.simplemc.simplehealthbars2.healthbar.ScoreboardHealthbar
import org.simplemc.simplehealthbars2.healthbar.StringHealthbar

/**
 * SimpleHealthbars2 plugin
 */
class SimpleHealthbars2 : JavaPlugin() {
    private lateinit var listener: DamageListener

    override fun onEnable() {
        // ensure config file exists
        saveDefaultConfig()

        listener = DamageListener(
            this,
            config.getConfigurationSection("player-bar")?.let {
                loadBar(it)?.let { bar ->
                    checkNotNull(bar as? PlayerHealthbar) { "Invalid player healthbar type! Must be one of: SCOREBOARD, ACTION" }
                }
            },
            config.getConfigurationSection("mob-bar")?.let {
                loadBar(it)?.let { bar ->
                    checkNotNull(bar as? MobHealthbar) { "Invalid mob healthbar type! Must be one of: NAME, ACTION" }
                }
            }
        )

        server.pluginManager.registerEvents(
            listener,
            this
        )

        logger.info("${description.name} version ${description.version} enabled!")
    }

    private fun loadBar(config: ConfigurationSection) =
        when (Healthbar.Type.valueOf(checkNotNull(config.getString("type")))) {
            Healthbar.Type.NAME -> NameHealthbar(loadStringBar(config))
            Healthbar.Type.ACTION -> ActionHealthbar(loadStringBar(config))
            Healthbar.Type.SCOREBOARD -> ScoreboardHealthbar(
                ScoreboardHealthbar.Config(
                    style = Healthbar.Style.valueOf(checkNotNull(config.getString("style", "ABSOLUTE")))
                )
            )
            Healthbar.Type.NONE -> null
        }

    private fun loadStringBar(config: ConfigurationSection) = StringHealthbar.Config(
        style = Healthbar.Style.valueOf(checkNotNull(config.getString("style", "BAR"))),
        length = config.getInt("length", 20),
        char = config.getInt("char", 0x25ae).toChar(),
        showMobNames = config.getBoolean("showMobNames", true)
    )

    override fun onDisable() {
        listener.close()
        logger.info("${description.name} disabled.")
    }
}
