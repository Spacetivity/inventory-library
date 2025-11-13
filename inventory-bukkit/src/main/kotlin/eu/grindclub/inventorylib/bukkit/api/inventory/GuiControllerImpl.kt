package eu.grindclub.inventorylib.bukkit.api.inventory

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import eu.grindclub.inventorylib.api.inventory.GuiController
import eu.grindclub.inventorylib.api.inventory.GuiProperties
import eu.grindclub.inventorylib.api.inventory.GuiProvider
import eu.grindclub.inventorylib.api.item.GuiItem
import eu.grindclub.inventorylib.api.item.GuiPos
import eu.grindclub.inventorylib.api.pagination.GuiPagination
import eu.grindclub.inventorylib.api.utils.MathUtils
import eu.grindclub.inventorylib.bukkit.api.pagination.GuiPaginationImpl
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import java.util.concurrent.ThreadLocalRandom

class GuiControllerImpl(override val provider: GuiProvider) : GuiController {

    override val properties: GuiProperties = provider.javaClass.getAnnotation(GuiProperties::class.java)

    override val inventorySlotCount: Int = properties.rows * properties.columns
    override var isCloseable: Boolean = properties.closeable

    override val contents: MutableMap<GuiPos, GuiItem?> = mutableMapOf()
    override var pagination: GuiPagination? = null
    override var rawInventory: Inventory? = null

    override var overriddenInventoryId: String? = null
    override var overriddenRows: Int = 0
    override var overriddenColumns: Int = 0

    override fun getInventoryId(): String {
        return if (overriddenInventoryId == null) this.properties.id else overriddenInventoryId!!
    }

    override fun getRows(): Int {
        return if (overriddenRows == 0) this.properties.rows else overriddenRows
    }

    override fun getColumns(): Int {
        return if (overriddenColumns == 0) this.properties.columns else overriddenColumns
    }

    override fun placeholder(pos: GuiPos, type: Material) {
        setItem(pos, GuiItem.placeholder(type))
    }

    override fun placeholder(row: Int, column: Int, type: Material) {
        setItem(row, column, GuiItem.placeholder(type))
    }

    override fun setItem(pos: GuiPos, item: GuiItem) {
        contents.replace(pos, item)
    }

    override fun setItem(row: Int, column: Int, item: GuiItem) {
        contents.replace(GuiPos.of(row, column), item)
    }

    override fun addItem(item: GuiItem) {
        val emptyPosition: GuiPos = getFirstEmptyPosition() ?: return
        setItem(emptyPosition, item)
    }

    override fun addItemToRandomPosition(item: GuiItem) {
        val randomSlotIndex = ThreadLocalRandom.current().nextInt(inventorySlotCount)
        val randomPosition: GuiPos = MathUtils.slotToPosition(randomSlotIndex, getColumns())
        if (isPositionTaken(randomPosition)) return
        setItem(randomPosition, item)
    }

    override fun removeItem(name: String) {
        val tempEntries: Set<Map.Entry<GuiPos, GuiItem?>> =
            contents.entries
        for ((position, value) in tempEntries) {
            val guiItem: GuiItem = value ?: continue

            val serializer: PlainTextComponentSerializer = PlainTextComponentSerializer.plainText()
            if (!serializer.serialize(guiItem.item.displayName()).equals(name, true)) continue

            contents.replace(position, null)
            this.rawInventory?.remove(guiItem.item)
        }
    }

    override fun removeItem(type: Material) {
        val tempEntries: Set<Map.Entry<GuiPos, GuiItem?>> = contents.entries

        for ((position, value) in tempEntries) {
            val guiItem: GuiItem = value ?: return
            if (guiItem.item.type != type) continue

            contents.replace(position, null)
        }
    }

    override fun fill(fillType: GuiController.FillType, item: GuiItem, vararg positions: GuiPos) {
        val rows: Int = getRows()
        val columns: Int = getColumns()

        when (fillType) {
            GuiController.FillType.ROW -> {
                require(positions.size <= 1) { "To fill a row only 1 position is allowed. Used positions: " + positions.size }

                val startSlot: Int = MathUtils.positionToSlot(positions[0], getColumns())

                for (currentSlot in startSlot until (startSlot + columns)) {
                    setItem(MathUtils.slotToPosition(currentSlot, columns), item)
                }
            }

            GuiController.FillType.RECTANGLE -> {
                require(positions.size == 2) { "Only two positions are allowed to create a rectangle!" }

                val fromPos: GuiPos = positions[0]
                val toPos: GuiPos = positions[1]

                val fromRow: Int = fromPos.row
                val fromColumn: Int = fromPos.column
                val toRow: Int = toPos.row
                val toColumn: Int = toPos.column

                for (row in fromRow..toRow) {
                    for (col in fromColumn..toColumn) {
                        setItem(GuiPos.of(row, col), item)
                    }
                }
            }

            GuiController.FillType.LEFT_BORDER -> {
                var currentSlot = 0
                while (currentSlot < this.inventorySlotCount) {
                    val currentPosition: GuiPos = MathUtils.slotToPosition(currentSlot, columns)
                    setItem(currentPosition, item)
                    currentSlot += rows
                }
            }

            GuiController.FillType.RIGHT_BORDER -> {
                val lastColumnStart = rows - 1
                val lastColumnEnd = this.inventorySlotCount - 1
                var currentSlot = lastColumnStart
                while (currentSlot <= lastColumnEnd) {
                    val currentPos: GuiPos = MathUtils.slotToPosition(currentSlot, columns)
                    val nextPos: GuiPos = MathUtils.nextPositionFromSlot(currentSlot, columns)

                    if (currentPos.row == nextPos.row) {
                        currentSlot += rows
                        continue
                    }

                    val currentPosition: GuiPos = MathUtils.slotToPosition(currentSlot, columns)
                    setItem(currentPosition, item)
                    currentSlot += rows
                }
            }

            GuiController.FillType.TOP_BORDER -> {
                for (currentSlot in 0 until columns) {
                    val currentPosition: GuiPos = MathUtils.slotToPosition(currentSlot, columns)
                    setItem(currentPosition, item)
                }
            }

            GuiController.FillType.BOTTOM_BORDER -> {
                val size = this.inventorySlotCount
                val firstColumnInLastRow = size - columns
                for (currentSlot in firstColumnInLastRow until size) {
                    val currentPosition: GuiPos = MathUtils.slotToPosition(currentSlot, columns)
                    setItem(currentPosition, item)
                }
            }

            GuiController.FillType.ALL_BORDERS -> {
                fill(GuiController.FillType.TOP_BORDER, item)
                fill(GuiController.FillType.RIGHT_BORDER, item)
                fill(GuiController.FillType.BOTTOM_BORDER, item)
                fill(GuiController.FillType.LEFT_BORDER, item)
            }
        }
    }

    override fun clearPosition(pos: GuiPos) {
        contents.replace(pos, null)
        rawInventory?.clear(MathUtils.positionToSlot(pos, getColumns()))
    }

    override fun isPositionTaken(pos: GuiPos): Boolean {
        return contents[pos] != null
    }

    override fun getPositionOfItem(item: GuiItem): GuiPos? {
        return contents.entries.filter { it.value == item }.map { it.key }.firstOrNull()
    }

    override fun getFirstEmptyPosition(): GuiPos? {
        var emptyPosition: GuiPos? = null

        for (position in this.contents.keys) if (this.contents[position] == null) {
            emptyPosition = position
            break
        }

        return emptyPosition
    }

    override fun getItem(pos: GuiPos): GuiItem? {
        return contents[pos]
    }

    override fun getItem(row: Int, column: Int): GuiItem? {
        return contents[GuiPos.of(row, column)]
    }

    override fun findFirstItemWithType(type: Material): GuiItem? {
        var result: GuiItem? = null

        for (slot in 0..inventorySlotCount) {
            val currentPosition: GuiPos = MathUtils.slotToPosition(slot, getColumns())
            if (contents[currentPosition] == null) continue
            if (contents[currentPosition]?.item == null) continue
            if (contents[currentPosition]?.item?.type !== type) continue
            result = contents[currentPosition]
        }

        return result
    }

    override fun createPagination(): GuiPagination {
        if (this.pagination == null) this.pagination = GuiPaginationImpl(this)
        return this.pagination!!
    }

    override fun updateRawInventory() {
        for ((position, value) in this.contents) {
            val guiItem: GuiItem = value ?: continue
            rawInventory?.setItem(MathUtils.positionToSlot(position, getColumns()), guiItem.item)
        }
    }

}

