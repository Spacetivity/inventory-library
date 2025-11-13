package eu.grindclub.inventorylib.bukkit.api

import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.InventoryApi
import eu.grindclub.inventorylib.api.inventory.InventoryHandler
import eu.grindclub.inventorylib.bukkit.api.inventory.ConfirmationInventory
import eu.grindclub.inventorylib.bukkit.api.inventory.InventoryHandlerImpl
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class InventoryApiImpl : InventoryApi {

    override val inventoryHandler: InventoryHandler = InventoryHandlerImpl()

    override fun openConfirmationInventory(holder: Player, title: Component, displayItem: ItemStack, onAccept: ((ItemStack) -> Unit), onDeny: ((ItemStack) -> Unit)) {
        inventoryHandler.openStaticInventory(
            holder = holder,
            title = title,
            provider = ConfirmationInventory(displayItem, onAccept, onDeny),
            forceSyncOpening = true
        )
    }

}
