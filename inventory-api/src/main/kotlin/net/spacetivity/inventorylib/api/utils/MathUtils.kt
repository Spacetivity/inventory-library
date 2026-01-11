package net.spacetivity.inventorylib.api.utils

import net.spacetivity.inventorylib.api.item.GuiPos

object MathUtils {

    fun slotToPosition(slot: Int, columns: Int): GuiPos {
        return GuiPos.of(slot / columns, slot % columns)
    }

    fun positionToSlot(pos: GuiPos, columns: Int): Int {
        return pos.row * columns + pos.column
    }

    fun nextPositionFromSlot(slot: Int, columns: Int): GuiPos {
        return slotToPosition(slot + 1, columns)
    }

}
