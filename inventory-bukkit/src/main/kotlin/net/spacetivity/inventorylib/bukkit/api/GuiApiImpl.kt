package net.spacetivity.inventorylib.bukkit.api

import net.kyori.adventure.text.Component
import net.spacetivity.inventorylib.api.GuiApi
import net.spacetivity.inventorylib.api.extension.getInventory
import net.spacetivity.inventorylib.api.inventory.GuiHandler
import net.spacetivity.inventorylib.api.item.GuiItem
import net.spacetivity.inventorylib.api.item.GuiPos
import net.spacetivity.inventorylib.api.pagination.GuiPagination
import net.spacetivity.inventorylib.bukkit.api.inventory.ConfirmationGui
import net.spacetivity.inventorylib.bukkit.api.inventory.GuiHandlerImpl
import net.spacetivity.inventorylib.bukkit.api.item.GuiItemImpl
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*

class GuiApiImpl : GuiApi {

    override val inventoryHandler: GuiHandler = GuiHandlerImpl()

    override fun placeholder(type: Material): GuiItem {
        val itemId = UUID.randomUUID().toString().split("-")[0]
        return of(makeItemStack(type, itemId)) { _, _, _ -> }
    }

    override fun nextPage(item: ItemStack, pagination: GuiPagination): GuiItem {
        return of(item) { _: GuiPos, _: GuiItem, _: InventoryClickEvent? -> pagination.toNextPage() }
    }

    override fun previousPage(item: ItemStack, pagination: GuiPagination): GuiItem {
        return of(item) { _: GuiPos, _: GuiItem, _: InventoryClickEvent -> pagination.toPreviousPage() }
    }

    override fun navigator(item: ItemStack, inventoryKey: String): GuiItem {
        return of(item) { _: GuiPos, _: GuiItem, event: InventoryClickEvent ->
            val holder: Player = event.whoClicked as Player
            val inventory = getInventory(holder, inventoryKey) ?: return@of

            holder.closeInventory()
            inventory.open(holder)
        }
    }

    override fun of(item: ItemStack): GuiItem {
        return GuiItemImpl(item) { _: GuiPos, _: GuiItem, _: InventoryClickEvent -> }
    }

    override fun of(item: ItemStack, action: ((GuiPos, GuiItem, InventoryClickEvent) -> Unit)): GuiItem {
        return GuiItemImpl(item, action)
    }

    override fun openConfirmationInventory(holder: Player, title: Component, displayItem: ItemStack, onAccept: ((ItemStack) -> Unit), onDeny: ((ItemStack) -> Unit)) {
        inventoryHandler.openStaticView(
            holder = holder,
            title = title,
            provider = ConfirmationGui(displayItem, onAccept, onDeny),
            forceSyncOpening = true
        )
    }

    private fun makeItemStack(type: Material, itemId: String): ItemStack {
        val itemStack = ItemStack(type)
        val itemMeta: ItemMeta = itemStack.itemMeta
        itemMeta.displayName(Component.text(" "))

        val namespacedKey = NamespacedKey("item", "itemid")
        val dataContainer: PersistentDataContainer = itemMeta.persistentDataContainer
        if (!dataContainer.has(namespacedKey, PersistentDataType.STRING)) {
            dataContainer.set(namespacedKey, PersistentDataType.STRING, itemId)
            itemStack.setItemMeta(itemMeta)
        }

        itemStack.setItemMeta(itemMeta)
        return itemStack
    }
}

