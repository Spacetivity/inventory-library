package eu.grindclub.inventorylib.bukkit.api.item

import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.inventory.GuiController
import eu.grindclub.inventorylib.api.item.GuiItem
import eu.grindclub.inventorylib.api.item.GuiPos
import eu.grindclub.inventorylib.api.item.ItemEnchantment
import eu.grindclub.inventorylib.api.utils.MathUtils
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.time.Instant
import java.util.*

class GuiItemImpl(
    override var item: ItemStack,
    private val action: ((GuiPos, GuiItem, InventoryClickEvent) -> Unit)?
) : GuiItem {

    private val itemId: String = UUID.randomUUID().toString().split("-")[0]
    private var lastClickTime: Instant = Instant.EPOCH
    private var cooldownMillis: Long = 250

    override fun runAction(position: GuiPos, guiItem: GuiItem, event: InventoryClickEvent) {
        val now = Instant.now()

        if (lastClickTime != Instant.EPOCH && (now.toEpochMilli() - lastClickTime.toEpochMilli()) < cooldownMillis) {
            event.isCancelled = true
            return
        }

        lastClickTime = now
        action?.invoke(position, guiItem, event)
    }

    @Suppress("UNCHECKED_CAST")
    override fun update(controller: GuiController, modification: GuiItem.Modification, vararg values: Any) {
        if (values.size > 1) throw UnsupportedOperationException("There are no more than one value allowed! Current size: " + values.size)

        val inventoryPosition: GuiPos? = controller.getPositionOfItem(this)
        var modifiableItem: ItemStack
        var extraItem: ItemStack? = null

        if (inventoryPosition != null) {
            val slot: Int = MathUtils.positionToSlot(inventoryPosition, controller.getColumns())
            val rawInventory: Inventory = controller.rawInventory!!
            modifiableItem = rawInventory.getItem(slot) ?: this.item
        } else {
            modifiableItem = this.item
        }

        if (controller.pagination != null)
            extraItem = controller.pagination!!.items.values().find { it is GuiItemImpl && it.itemId == this.itemId }?.item

        val newValue = values[0]

        when (modification) {
            GuiItem.Modification.TYPE -> {
                if (newValue !is Material) throw UnsupportedOperationException("'newValue' is not a Material!")

                modifiableItem = modifiableItem.withType(newValue)
                this.item = modifiableItem

                if (inventoryPosition != null) {
                    val slot: Int = MathUtils.positionToSlot(inventoryPosition, controller.getColumns())
                    controller.rawInventory!!.setItem(slot, modifiableItem)
                }

                extraItem = extraItem?.withType(newValue)
                if (extraItem != null) {
                    val paginationItem = controller.pagination?.items?.values()?.find { it is GuiItemImpl && it.itemId == this.itemId } as? GuiItemImpl
                    paginationItem?.item = extraItem
                }
            }

            GuiItem.Modification.DISPLAY_NAME -> {
                if (newValue !is Component) throw UnsupportedOperationException("'newValue' is not a Component!")

                modifiableItem.editMeta { itemMeta: ItemMeta -> itemMeta.displayName(newValue) }
                extraItem?.editMeta { itemMeta: ItemMeta -> itemMeta.displayName(newValue) }
            }

            GuiItem.Modification.LORE -> {
                if (newValue !is MutableList<*>) throw UnsupportedOperationException("'newValue' is not a List!")

                modifiableItem.editMeta { it.lore(newValue as MutableList<Component>) }
                extraItem?.editMeta { it.lore(newValue as MutableList<Component>) }
            }

            GuiItem.Modification.AMOUNT -> {
                if (newValue !is Int) throw UnsupportedOperationException("'newValue' is not an Integer!")

                modifiableItem.amount = newValue
                if (extraItem != null) extraItem.amount = newValue
            }

            GuiItem.Modification.INCREMENT -> {
                if (newValue !is Int) throw UnsupportedOperationException("'newValue' is not an Integer!")

                modifiableItem.amount += newValue
                if (extraItem != null) extraItem.amount += newValue
            }

            GuiItem.Modification.ENCHANTMENTS -> {
                if (newValue !is ItemEnchantment) throw UnsupportedOperationException("'newValue' is not an ItemEnchantment!")

                newValue.performAction(modifiableItem)
                if (extraItem != null) newValue.performAction(extraItem)
            }

            GuiItem.Modification.GLOWING -> {
                if (newValue !is Boolean) throw UnsupportedOperationException("'newValue' is not an Boolean!")

                modifiableItem.editMeta {
                    it.setEnchantmentGlintOverride(newValue)
                }
                extraItem?.editMeta {
                    it.setEnchantmentGlintOverride(newValue)
                }
            }
        }
    }
}


