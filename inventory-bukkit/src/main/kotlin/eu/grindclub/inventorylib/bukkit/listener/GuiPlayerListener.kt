package eu.grindclub.inventorylib.bukkit.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import eu.grindclub.inventorylib.api.inventory.GuiHandler
import eu.grindclub.inventorylib.api.inventory.GuiInventory
import eu.grindclub.inventorylib.api.item.GuiItem
import eu.grindclub.inventorylib.api.utils.MathUtils
import eu.grindclub.inventorylib.bukkit.GuiInventoryBukkit
import eu.grindclub.inventorylib.bukkit.utils.SoundUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent

class GuiPlayerListener(private val plugin: GuiInventoryBukkit) : Listener {

    private val inventoryHandler: GuiHandler

    init {
        plugin.server.pluginManager.registerEvents(this, this.plugin)
        this.inventoryHandler = eu.grindclub.inventorylib.api.GuiInventoryProvider.api.inventoryHandler
    }

    @EventHandler
    fun onPlayerInventoryClick(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as Player

        if (!player.hasMetadata("open-inventory")) return
        if (event.clickedInventory !== player.openInventory.topInventory) return
        if (!validateInventory(player, event.view.title())) return

        val inventory: GuiInventory = inventoryHandler.getInventory(player, getOpenInventoryName(player)) ?: return
        val position: eu.grindclub.inventorylib.api.item.GuiPos = MathUtils.slotToPosition(event.slot, inventory.columns)

        event.isCancelled = true

        val currentItem: GuiItem = inventory.controller.getItem(position) ?: return
        currentItem.runAction(position, currentItem, event)

        if (GuiInventoryBukkit.instance.soundConfigFile.onClick != null)
        if (inventory.controller.properties.playSoundOnClick) SoundUtils.playClickSound(player)
    }

    @EventHandler
    fun onPlayerInventoryClose(event: InventoryCloseEvent) {
        if (event.inventory.holder !is Player) return
        val player: Player = event.player as Player

        if (!player.hasMetadata("open-inventory")) return
        if (!validateInventory(player, event.view.title())) return

        val inventory = inventoryHandler.getInventory(player, getOpenInventoryName(player)) ?: return

        if (!inventory.isCloseable) {
            Bukkit.getScheduler().runTask(this.plugin, Runnable { inventory.open(player) })
        } else {
            player.removeMetadata("open-inventory", this.plugin)
            if (inventory.controller.properties.playSoundOnClose) SoundUtils.playCloseSound(player)
            if (inventory.isStaticInventory) inventoryHandler.removeCachedInventory(player, inventory)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        inventoryHandler.clearCachedInventories(event.player)
    }

    private fun validateInventory(player: Player, title: Component): Boolean {
        val openInventoryTitle: String = PlainTextComponentSerializer.plainText().serialize(title)

        val inventoryName: String = getOpenInventoryName(player)
        val inventory: GuiInventory = inventoryHandler.getInventory(player, inventoryName) ?: return false
        val possibleInventoryTitle: String = PlainTextComponentSerializer.plainText().serialize(inventory.title)

        return possibleInventoryTitle.equals(openInventoryTitle, true)
    }

    private fun getOpenInventoryName(player: Player): String {
        return player.getMetadata("open-inventory")[0].value() as String
    }
}

