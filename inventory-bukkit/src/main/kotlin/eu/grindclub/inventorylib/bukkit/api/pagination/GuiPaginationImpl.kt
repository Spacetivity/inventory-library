package eu.grindclub.inventorylib.bukkit.api.pagination

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import eu.grindclub.inventorylib.api.inventory.GuiController
import eu.grindclub.inventorylib.api.item.GuiItem
import eu.grindclub.inventorylib.api.item.GuiPos
import eu.grindclub.inventorylib.api.pagination.GuiPagination
import eu.grindclub.inventorylib.bukkit.utils.SoundUtils
import org.bukkit.entity.Player


class GuiPaginationImpl(private val controller: GuiController) : GuiPagination {

    override val positions: MutableList<GuiPos> = mutableListOf()
    override val items: Multimap<Int, GuiItem> = ArrayListMultimap.create()

    private var currentPageId: Int = 0
    private var itemsPerPage: Int = 9

    override fun getLastPageId(): Int {
        val pageIds = items.keySet().stream().toList()
        return pageIds[pageIds.size - 1]
    }

    override fun getPageAmount(): Int {
        return items.keySet().stream().toList().size
    }

    override fun getCurrentPageId(): Int {
        return this.currentPageId
    }

    override fun isFirstPage(): Boolean {
        return this.currentPageId == 0
    }

    override fun isLastPage(): Boolean {
        val pageIds = items.keySet().stream().toList()
        return this.currentPageId == pageIds[pageIds.size - 1]
    }

    override fun page(pageId: Int) {
        val pageIds = items.keySet().stream().toList()
        this.currentPageId = if ((pageIds.size < pageId || pageId < 0)) 0 else pageId
        refreshPage()
    }

    override fun toFirstPage() {
        this.currentPageId = 0
        refreshPage()
    }

    override fun toLastPage() {
        if (items.isEmpty) {
            this.currentPageId = 0
        } else {
            val pageIds = items.keySet().stream().toList()
            this.currentPageId = pageIds[pageIds.size - 1]
        }
        refreshPage()
    }

    override fun toNextPage() {
        if (items.isEmpty) return
        if (isLastPage()) return
        this.currentPageId += 1
        refreshPage()
    }

    override fun toPreviousPage() {
        if (items.isEmpty) return
        if (isFirstPage()) return
        this.currentPageId -= 1
        refreshPage()
    }

    override fun setItemField(startRow: Int, startColumn: Int, endRow: Int, endColumn: Int) {
        val numRows = controller.getRows()
        val numColumns = controller.getColumns()

        for (row in startRow until numRows.coerceAtMost(endRow + 1)) {
            for (column in startColumn until numColumns.coerceAtMost(endColumn + 1)) {
                val position = GuiPos.of(row, column)
                positions.add(position)
            }
        }
    }


    override fun distributeItems(items: List<GuiItem>) {
        this.items.clear()
        var pageIndex = 0

        for (i in items.indices) {
            if (i % itemsPerPage == 0) pageIndex = i / itemsPerPage
            this.items.put(pageIndex, items[i])
        }

        refreshPage()
    }


    override fun limitItemsPerPage(amount: Int) {
        this.itemsPerPage = amount
    }

    override fun refreshPage() {
        for (currentPosition in this.positions) {
            if (!controller.isPositionTaken(currentPosition)) continue
            controller.clearPosition(currentPosition)
        }

        val itemsForNextPage: List<GuiItem> = items[currentPageId].stream().toList()
        var itemIndex = 0

        for (currentPosition in this.positions) {
            if (itemIndex >= itemsForNextPage.size) break
            if (controller.isPositionTaken(currentPosition)) continue
            controller.setItem(currentPosition, itemsForNextPage[itemIndex])
            itemIndex++
        }

        controller.updateRawInventory()

        if (controller.properties.playSoundOnPageSwitch) {
            val holder = controller.rawInventory?.holder ?: return
            SoundUtils.playSwitchPageSound(holder as Player)
        }
    }
}

