package eu.grindclub.inventorylib.api.inventory

import com.google.common.collect.Multimap
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface GuiHandler {

    val inventories: Multimap<Player, GuiView>

    fun openStaticView(holder: Player, title: Component, provider: Gui, forceSyncOpening: Boolean)

    fun cacheView(holder: Player, title: Component, provider: Gui)

    fun cacheView(
        holder: Player,
        title: Component,
        provider: Gui,
        staticInventory: Boolean
    ): GuiView?

    fun updateCachedView(holder: Player, viewId: String)

    fun clearCachedViews(holder: Player)

    fun removeCachedView(holder: Player, view: GuiView)

    fun getView(holder: Player, name: String): GuiView?

}

