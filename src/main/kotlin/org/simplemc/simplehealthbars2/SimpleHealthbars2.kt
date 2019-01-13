package org.simplemc.simplehealthbars2

import org.bukkit.plugin.java.JavaPlugin

/**
 * SimpleHealthbars2 plugin
 */
class SimpleHealthbars2 : JavaPlugin() {
    override fun onEnable() {
        // ensure config file exists
        saveDefaultConfig()

        logger.info("${description.name} version ${description.version} enabled!")
    }

    override fun onDisable() = logger.info("${description.name} disabled.")
}
