package net.spacetivity.inventorylib.bukkit.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.spacetivity.inventorylib.api.inventory.GuiHandler
import net.spacetivity.inventorylib.api.GuiProvider
import net.spacetivity.inventorylib.api.inventory.GuiView
import net.spacetivity.inventorylib.api.item.GuiItem
import net.spacetivity.inventorylib.api.utils.MathUtils
import net.spacetivity.inventorylib.bukkit.GuiInventoryBukkit
import net.spacetivity.inventorylib.bukkit.utils.SoundUtils
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType

class GuiPlayerListener(private val plugin: GuiInventoryBukkit) : Listener {

    private val inventoryHandler: GuiHandler

    companion object {
        private val OPEN_INVENTORY_KEY: NamespacedKey by lazy {
            NamespacedKey(GuiInventoryBukkit.instance, "open-inventory")
        }
    }

    init {
        plugin.server.pluginManager.registerEvents(this, this.plugin)
        this.inventoryHandler = GuiProvider.api.inventoryHandler
    }

    @EventHandler
    fun onPlayerInventoryClick(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as Player

        if (!player.persistentDataContainer.has(OPEN_INVENTORY_KEY, PersistentDataType.STRING)) return
        if (event.clickedInventory !== player.openInventory.topInventory) return
        if (!validateInventory(player, event.view.title())) return

        val inventory: GuiView = inventoryHandler.getView(player, getOpenInventoryName(player)) ?: return
        val position: net.spacetivity.inventorylib.api.item.GuiPos = MathUtils.slotToPosition(event.slot, inventory.columns)

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

        if (!player.persistentDataContainer.has(OPEN_INVENTORY_KEY, PersistentDataType.STRING)) return
        if (!validateInventory(player, event.view.title())) return

        val inventory = inventoryHandler.getView(player, getOpenInventoryName(player)) ?: return

        if (!inventory.isCloseable) {
            Bukkit.getScheduler().runTask(this.plugin, Runnable { inventory.open(player) })
        } else {
            player.persistentDataContainer.remove(OPEN_INVENTORY_KEY)
            if (inventory.controller.properties.playSoundOnClose) SoundUtils.playCloseSound(player)
            if (inventory.isStaticInventory) inventoryHandler.removeCachedView(player, inventory)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        inventoryHandler.clearCachedViews(event.player)
    }

    private fun validateInventory(player: Player, title: Component): Boolean {
        val openInventoryTitle: String = PlainTextComponentSerializer.plainText().serialize(title)

        val inventoryName: String = getOpenInventoryName(player)
        val inventory: GuiView = inventoryHandler.getView(player, inventoryName) ?: return false
        val possibleInventoryTitle: String = PlainTextComponentSerializer.plainText().serialize(inventory.title)

        return possibleInventoryTitle.equals(openInventoryTitle, true)
    }

    private fun getOpenInventoryName(player: Player): String {
        return player.persistentDataContainer.get(OPEN_INVENTORY_KEY, PersistentDataType.STRING) ?: ""
    }
}

