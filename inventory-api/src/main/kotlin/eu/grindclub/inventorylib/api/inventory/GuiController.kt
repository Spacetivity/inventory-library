package eu.grindclub.inventorylib.api.inventory

import eu.grindclub.inventorylib.api.item.GuiItem
import eu.grindclub.inventorylib.api.item.GuiPos
import eu.grindclub.inventorylib.api.pagination.GuiPagination
import org.bukkit.Material
import org.bukkit.inventory.Inventory

interface GuiController {

    val provider: GuiProvider
    val properties: GuiProperties

    val inventorySlotCount: Int
    var isCloseable: Boolean

    val contents: MutableMap<GuiPos, GuiItem?>
    val pagination: GuiPagination?
    var rawInventory: Inventory?

    var overriddenInventoryId: String?
    var overriddenRows: Int
    var overriddenColumns: Int

    fun getInventoryId(): String
    fun getRows(): Int
    fun getColumns(): Int

    fun placeholder(pos: GuiPos, type: Material)
    fun placeholder(row: Int, column: Int, type: Material)

    fun setItem(pos: GuiPos, item: GuiItem)
    fun setItem(row: Int, column: Int, item: GuiItem)
    fun addItem(item: GuiItem)
    fun addItemToRandomPosition(item: GuiItem)
    fun removeItem(name: String)
    fun removeItem(type: Material)

    fun fill(fillType: FillType, item: GuiItem, vararg positions: GuiPos)
    fun clearPosition(pos: GuiPos)

    fun isPositionTaken(pos: GuiPos): Boolean
    fun getPositionOfItem(item: GuiItem): GuiPos?
    fun getFirstEmptyPosition(): GuiPos?

    fun getItem(pos: GuiPos): GuiItem?
    fun getItem(row: Int, column: Int): GuiItem?
    fun findFirstItemWithType(type: Material): GuiItem?

    fun createPagination(): GuiPagination

    fun updateRawInventory()

    enum class FillType {
        ROW,
        RECTANGLE,
        LEFT_BORDER,
        RIGHT_BORDER,
        TOP_BORDER,
        BOTTOM_BORDER,
        ALL_BORDERS
    }
}

