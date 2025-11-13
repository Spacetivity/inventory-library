package eu.grindclub.inventorylib.api.extension

import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.SpaceInventoryProvider
import eu.grindclub.inventorylib.api.inventory.InventoryProvider
import eu.grindclub.inventorylib.api.inventory.SpaceInventory
import org.bukkit.entity.Player

fun openStaticInventory(holder: Player, title: Component, provider: InventoryProvider) {
    SpaceInventoryProvider.api.inventoryHandler.openStaticInventory(holder, title, provider, true)
}

fun openInventory(holder: Player, key: String) {
    SpaceInventoryProvider.api.inventoryHandler.getInventory(holder, key)?.open(holder)
}

fun getInventory(holder: Player, key: String): SpaceInventory? {
    return SpaceInventoryProvider.api.inventoryHandler.getInventory(holder, key)
}

fun cacheInventory(holder: Player, title: Component, provider: InventoryProvider) {
    SpaceInventoryProvider.api.inventoryHandler.cacheInventory(holder, title, provider)
}

fun removeCachedInventory(holder: Player, inventory: SpaceInventory) {
    SpaceInventoryProvider.api.inventoryHandler.removeCachedInventory(holder, inventory)
}

fun clearCachedInventories(holder: Player) {
    SpaceInventoryProvider.api.inventoryHandler.clearCachedInventories(holder)
}

fun updateCachedInventory(holder: Player, key: String) {
    SpaceInventoryProvider.api.inventoryHandler.updateCachedInventory(holder, key)
}