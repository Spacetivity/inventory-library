package net.spacetivity.inventorylib.bukkit.api.inventory

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.inventorylib.api.inventory.GuiController
import net.spacetivity.inventorylib.api.inventory.GuiProperties
import net.spacetivity.inventorylib.api.inventory.Gui as InventoryGui
import net.spacetivity.inventorylib.api.item.GuiItem
import net.spacetivity.inventorylib.api.GuiProvider
import net.spacetivity.inventorylib.api.item.GuiPos
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@GuiProperties(id = "confirmation_inv", rows = 3, columns = 9, closeable = true)
class ConfirmationGui(
    private val displayItem: ItemStack,
    private val onAccept: ((ItemStack) -> Unit),
    private val onDeny: ((ItemStack) -> Unit)
) : InventoryGui {

    override fun init(player: Player, controller: GuiController) {
        val acceptItem = ItemStack(Material.LIME_STAINED_GLASS_PANE)
        acceptItem.editMeta { itemMeta: ItemMeta ->
            itemMeta.displayName(Component.text("✔", NamedTextColor.GREEN))
        }

        val denyItem = ItemStack(Material.RED_STAINED_GLASS_PANE)
        denyItem.editMeta { itemMeta: ItemMeta ->
            itemMeta.displayName(Component.text("✗", NamedTextColor.RED))
        }

        controller.fill(
            GuiController.FillType.RECTANGLE,
            GuiProvider.api.of(acceptItem) { _, _, _ -> onAccept.invoke(acceptItem) },
            GuiPos.of(0, 0),
            GuiPos.of(2, 2)
        )

        controller.fill(
            GuiController.FillType.RECTANGLE,
            GuiProvider.api.of(denyItem) { _, _, _ -> onDeny.invoke(denyItem) },
            GuiPos.of(0, 6),
            GuiPos.of(2, 8)
        )

        controller.setItem(1, 4, GuiProvider.api.of(this.displayItem))
    }
}

