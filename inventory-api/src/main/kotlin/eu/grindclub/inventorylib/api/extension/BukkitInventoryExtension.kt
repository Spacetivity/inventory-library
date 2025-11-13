package eu.grindclub.inventorylib.api.extension

import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.GuiInventoryProvider
import eu.grindclub.inventorylib.api.inventory.GuiProvider
import eu.grindclub.inventorylib.api.inventory.GuiInventory
import org.bukkit.entity.Player

fun openStaticInventory(holder: Player, title: Component, provider: GuiProvider) {
    GuiInventoryProvider.api.inventoryHandler.openStaticInventory(holder, title, provider, true)
}

fun openInventory(holder: Player, key: String) {
    GuiInventoryProvider.api.inventoryHandler.getInventory(holder, key)?.open(holder)
}

fun getInventory(holder: Player, key: String): GuiInventory? {
    return GuiInventoryProvider.api.inventoryHandler.getInventory(holder, key)
}

fun cacheInventory(holder: Player, title: Component, provider: GuiProvider) {
    GuiInventoryProvider.api.inventoryHandler.cacheInventory(holder, title, provider)
}

fun removeCachedInventory(holder: Player, inventory: GuiInventory) {
    GuiInventoryProvider.api.inventoryHandler.removeCachedInventory(holder, inventory)
}

fun clearCachedInventories(holder: Player) {
    GuiInventoryProvider.api.inventoryHandler.clearCachedInventories(holder)
}

fun updateCachedInventory(holder: Player, key: String) {
    GuiInventoryProvider.api.inventoryHandler.updateCachedInventory(holder, key)
}