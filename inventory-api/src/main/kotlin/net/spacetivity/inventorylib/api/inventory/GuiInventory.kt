package net.spacetivity.inventorylib.api.inventory

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface GuiView {

    val provider: Gui

    val controller: GuiController

    val name: String

    val title: Component

    val rows: Int

    val columns: Int

    val isCloseable: Boolean

    val isStaticInventory: Boolean

    fun open(holder: Player)
    fun open(holder: Player, pageId: Int)

    fun open(holder: Player, forceSyncOpening: Boolean)
    fun open(holder: Player, pageId: Int, forceSyncOpening: Boolean)

    fun close(holder: Player)
    fun close(holder: Player, forceSyncClosing: Boolean)

}

