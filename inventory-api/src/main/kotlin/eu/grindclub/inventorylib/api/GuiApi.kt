package eu.grindclub.inventorylib.api

import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.inventory.GuiHandler
import eu.grindclub.inventorylib.api.item.GuiItem
import eu.grindclub.inventorylib.api.item.GuiPos
import eu.grindclub.inventorylib.api.pagination.GuiPagination
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface GuiApi {

    val inventoryHandler: GuiHandler

    fun placeholder(type: Material): GuiItem
    fun nextPage(item: ItemStack, pagination: GuiPagination): GuiItem
    fun previousPage(item: ItemStack, pagination: GuiPagination): GuiItem
    fun navigator(item: ItemStack, inventoryKey: String): GuiItem
    fun of(item: ItemStack): GuiItem
    fun of(item: ItemStack, action: ((GuiPos, GuiItem, InventoryClickEvent) -> Unit)): GuiItem

    fun openConfirmationInventory(
        holder: Player,
        title: Component,
        displayItem: ItemStack,
        onAccept: ((ItemStack) -> Unit),
        onDeny: ((ItemStack) -> Unit)
    )

}

