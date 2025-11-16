package eu.grindclub.inventorylib.api.item

import eu.grindclub.inventorylib.api.inventory.GuiController
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

interface GuiItem {

    var item: ItemStack

    fun runAction(position: GuiPos, guiItem: GuiItem, event: InventoryClickEvent)

    fun update(controller: GuiController, modification: Modification, vararg values: Any)

    enum class Modification {
        TYPE,
        DISPLAY_NAME,
        LORE,
        AMOUNT,
        INCREMENT,
        ENCHANTMENTS,
        GLOWING;
    }
}

