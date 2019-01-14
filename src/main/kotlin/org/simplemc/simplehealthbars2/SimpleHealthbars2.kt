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
            loadBar(config.getConfigurationSection("player-bar")) as PlayerHealthbar?,
            loadBar(config.getConfigurationSection("mob-bar")) as MobHealthbar?
        )

        server.pluginManager.registerEvents(
            listener,
            this
        )

        logger.info("${description.name} version ${description.version} enabled!")
    }

    private fun loadBar(config: ConfigurationSection) =
        when(Healthbar.Type.valueOf(config.getString("type"))) {
            Healthbar.Type.NAME -> NameHealthbar(loadStringBar(config))
            Healthbar.Type.ACTION -> ActionHealthbar(loadStringBar(config))
            Healthbar.Type.SCOREBOARD -> ScoreboardHealthbar(
                ScoreboardHealthbar.Config(style = Healthbar.Style.valueOf(config.getString("style", "ABSOLUTE")))
            )
            Healthbar.Type.NONE -> null
        }

    private fun loadStringBar(config: ConfigurationSection) = StringHealthbar.Config(
        style = Healthbar.Style.valueOf(config.getString("style", "BAR")),
        length = config.getInt("length", 20),
        char = config.getInt("char", 0x25ae).toChar(),
        showMobNames = config.getBoolean("showMobNames", true)
    )

    override fun onDisable() {
        listener.close()
        logger.info("${description.name} disabled.")
    }
}
