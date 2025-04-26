package org.simplemc.simplehealthbars2

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import org.simplemc.simplehealthbars2.healthbar.Healthbar
import org.simplemc.simplehealthbars2.healthbar.MobHealthbar
import org.simplemc.simplehealthbars2.healthbar.PlayerHealthbar
import java.util.UUID

class DamageListener(
    private val plugin: Plugin,
    private val playerHealthbars: Map<String?, PlayerHealthbar?>,
    private val mobHealthbars: Map<String?, MobHealthbar?>,
) : Listener, AutoCloseable {
    private data class RemoveHealthbarTask(val taskId: Int?, val removeAction: () -> Unit)

    private val scheduler = Bukkit.getScheduler()
    private val removeHealthbarTasks: MutableMap<UUID, RemoveHealthbarTask?> = mutableMapOf()

    // <editor-fold desc="Set always on healthbards on spawn/join">
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onEntitySpawn(event: EntitySpawnEvent) {
        val entity = event.entity as? LivingEntity
        entity?.healthbar?.let { healthbar ->
            if (healthbar.durationTicks == null) {
                healthbar(null, entity, 0.0)
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.healthbar?.let { healthbar ->
            if (healthbar.durationTicks == null) {
                healthbar(null, event.player, 0.0)
            }
        }
    }
    // </editor-fold>

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onEntityDamageEvent(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        val source = event.damager as? LivingEntity

        // put source and target healthbars
        healthbar(source, target, event.finalDamage)
        source?.let { healthbar(target, it, 0.0) }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onPlayerQuit(event: PlayerQuitEvent) = clearEntityHealthbar(event.player)

    /**
     * Remove the healthbar from dying entities immediately
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onEntityDeathEvent(event: EntityDeathEvent) = clearEntityHealthbar(event.entity)

    private fun healthbar(source: LivingEntity?, target: LivingEntity, damage: Double) {
        // cancel scheduled healthbar removal and run it now to prepare for new (updated) healthbar
        clearEntityHealthbar(target)

        // update healthbar and schedule its removal task if necessary
        val healthbar = target.healthbar
        healthbar?.updateHealth(source, target, damage)?.let { removeAction ->
            val taskId = healthbar.durationTicks?.let { ticks ->
                scheduler.scheduleSyncDelayedTask(plugin, removeAction, ticks)
            }
            removeHealthbarTasks[target.uniqueId] = RemoveHealthbarTask(taskId, removeAction)
        }
    }

    private val LivingEntity.healthbar: Healthbar?
        get(): Healthbar? = when (this) {
            is Player -> playerHealthbars[world.name] ?: playerHealthbars[null]
            else -> mobHealthbars[world.name] ?: mobHealthbars[null]
        }

    private fun clearEntityHealthbar(entity: LivingEntity) {
        removeHealthbarTasks.remove(entity.uniqueId)?.let {
            it.taskId?.let { taskId -> scheduler.cancelTask(taskId) }
            it.removeAction()
        }
    }

    /**
     * Remember to remove all healthbars on close
     */
    override fun close() {
        removeHealthbarTasks.forEach { (_, removeTask) -> removeTask?.removeAction() }
    }
}
