package eu.grindclub.inventorylib.api

import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.inventory.GuiHandler
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface GuiApi {

    val inventoryHandler: GuiHandler

    fun openConfirmationInventory(
        holder: Player,
        title: Component,
        displayItem: ItemStack,
        onAccept: ((ItemStack) -> Unit),
        onDeny: ((ItemStack) -> Unit)
    )

}

