package eu.grindclub.inventorylib.bukkit.api

import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.GuiApi
import eu.grindclub.inventorylib.api.inventory.GuiHandler
import eu.grindclub.inventorylib.bukkit.api.inventory.ConfirmationGui
import eu.grindclub.inventorylib.bukkit.api.inventory.GuiHandlerImpl
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GuiApiImpl : GuiApi {

    override val inventoryHandler: GuiHandler = GuiHandlerImpl()

    override fun openConfirmationInventory(holder: Player, title: Component, displayItem: ItemStack, onAccept: ((ItemStack) -> Unit), onDeny: ((ItemStack) -> Unit)) {
        inventoryHandler.openStaticInventory(
            holder = holder,
            title = title,
            provider = ConfirmationGui(displayItem, onAccept, onDeny),
            forceSyncOpening = true
        )
    }

}

