package net.spacetivity.inventorylib.api.dsl

import net.spacetivity.inventorylib.api.GuiProvider
import net.spacetivity.inventorylib.api.item.GuiItem
import net.spacetivity.inventorylib.api.item.GuiPos
import net.spacetivity.inventorylib.api.pagination.GuiPagination
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

fun guiItem(item: ItemStack): GuiItem {
    return GuiProvider.api.of(item)
}

fun guiItem(item: ItemStack, action: (GuiPos, GuiItem, InventoryClickEvent) -> Unit): GuiItem {
    return GuiProvider.api.of(item, action)
}

fun guiPlaceholder(type: Material): GuiItem {
    return GuiProvider.api.placeholder(type)
}

fun guiNavigator(item: ItemStack, inventoryKey: String): GuiItem {
    return GuiProvider.api.navigator(item, inventoryKey)
}

fun guiNextPage(item: ItemStack, pagination: GuiPagination): GuiItem {
    return GuiProvider.api.nextPage(item, pagination)
}

fun guiPreviousPage(item: ItemStack, pagination: GuiPagination): GuiItem {
    return GuiProvider.api.previousPage(item, pagination)
}

