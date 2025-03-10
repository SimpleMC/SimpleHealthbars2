package org.simplemc.simplehealthbars2

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.plugin.Plugin
import org.simplemc.simplehealthbars2.healthbar.MobHealthbar
import org.simplemc.simplehealthbars2.healthbar.PlayerHealthbar
import java.util.UUID

class DamageListener(
    private val plugin: Plugin,
    private val playerHealthbars: Map<String?, PlayerHealthbar?>,
    private val mobHealthbars: Map<String?, MobHealthbar?>,
) : Listener, AutoCloseable {
    private data class RemoveHealthbarTask(val taskId: Int, val task: () -> Unit)

    private val scheduler = Bukkit.getScheduler()
    private val removeHealthbarTasks: MutableMap<UUID, RemoveHealthbarTask?> = mutableMapOf()

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onEntityDamageEvent(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        val source = event.damager as? LivingEntity

        // put source and target healthbars
        healthbar(source, target, event.finalDamage)
        source?.let { healthbar(target, it, 0.0) }
    }

    /**
     * Remove the healthbar from dying entities immediately
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onEntityDeathEvent(event: EntityDeathEvent) {
        removeHealthbarTasks[event.entity.uniqueId]?.let {
            scheduler.cancelTask(it.taskId)
            it.task()
        }
    }

    private fun healthbar(source: LivingEntity?, target: LivingEntity, damage: Double) {
        // cancel scheduled healthbar removal and run it now to prepare for new (updated) healthbar
        removeHealthbarTasks[target.uniqueId]?.let {
            scheduler.cancelTask(it.taskId)
            it.task()
        }

        val healthbar = when (target) {
            is Player -> playerHealthbars[target.world.name] ?: playerHealthbars[null]
            else -> mobHealthbars[target.world.name] ?: mobHealthbars[null]
        }

        // update healthbar and schedule its removal task if available
        healthbar?.updateHealth(source, target, damage)?.let {
            val taskId = scheduler.scheduleSyncDelayedTask(plugin, it, healthbar.durationTicks)
            removeHealthbarTasks[target.uniqueId] = RemoveHealthbarTask(taskId, it)
        }
    }

    /**
     * Remember to remove all healthbars on close
     */
    override fun close() {
        removeHealthbarTasks
            .mapNotNull { (_, removeTask) -> removeTask?.task }
            .forEach { it() }
    }
}
