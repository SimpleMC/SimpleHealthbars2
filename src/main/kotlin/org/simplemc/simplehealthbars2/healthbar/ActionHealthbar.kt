package org.simplemc.simplehealthbars2.healthbar

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.simplemc.simplehealthbars2.getDisplayName

class ActionHealthbar(config: Config) : StringHealthbar(config), PlayerHealthbar, MobHealthbar {

    override fun updateHealth(source: LivingEntity?, target: LivingEntity, damage: Double): (() -> Unit)? {
        if (source != null && source is Player) {
            source.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacy(formatHealthbar(target, target.getDisplayName(), damage)),
            )
        }

        return null
    }
}
