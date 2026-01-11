package net.spacetivity.inventorylib.api.item

data class GuiPos(val row: Int, val column: Int) {

    companion object {
        fun of(row: Int, column: Int): GuiPos {
            return GuiPos(row, column)
        }
    }

}

