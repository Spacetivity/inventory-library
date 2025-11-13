package eu.grindclub.inventorylib.api.pagination

import com.google.common.collect.Multimap
import eu.grindclub.inventorylib.api.item.GuiItem

interface GuiPagination {

    val positions: List<Any>
    val items: Multimap<Int, GuiItem>

    fun getPaginationItems(): List<GuiItem> = this.items.entries().mapNotNull { it.value }

    fun getLastPageId(): Int
    fun getPageAmount(): Int
    fun getCurrentPageId(): Int

    fun isFirstPage(): Boolean
    fun isLastPage(): Boolean

    fun page(pageId: Int)

    fun toFirstPage()
    fun toLastPage()

    fun toNextPage()
    fun toPreviousPage()

    fun setItemField(startRow: Int, startColumn: Int, endRow: Int, endColumn: Int)
    fun distributeItems(items: List<GuiItem>)
    fun limitItemsPerPage(amount: Int)
    fun refreshPage()

}

