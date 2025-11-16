package eu.grindclub.inventorylib.api.extension

import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.GuiProvider
import eu.grindclub.inventorylib.api.inventory.Gui as InventoryGuiProvider
import eu.grindclub.inventorylib.api.inventory.GuiView
import org.bukkit.entity.Player

fun openStaticInventory(holder: Player, title: Component, provider: InventoryGuiProvider) {
    GuiProvider.api.inventoryHandler.openStaticView(holder, title, provider, true)
}

fun openInventory(holder: Player, key: String) {
    GuiProvider.api.inventoryHandler.getView(holder, key)?.open(holder)
}

fun getInventory(holder: Player, key: String): GuiView? {
    return GuiProvider.api.inventoryHandler.getView(holder, key)
}

fun cacheInventory(holder: Player, title: Component, provider: InventoryGuiProvider) {
    GuiProvider.api.inventoryHandler.cacheView(holder, title, provider)
}

fun removeCachedInventory(holder: Player, inventory: GuiView) {
    GuiProvider.api.inventoryHandler.removeCachedView(holder, inventory)
}

fun clearCachedInventories(holder: Player) {
    GuiProvider.api.inventoryHandler.clearCachedViews(holder)
}

fun updateCachedInventory(holder: Player, key: String) {
    GuiProvider.api.inventoryHandler.updateCachedView(holder, key)
}