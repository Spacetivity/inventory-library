package eu.grindclub.inventorylib.api.inventory

import com.google.common.collect.Multimap
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface GuiHandler {

    val inventories: Multimap<Player, GuiInventory>

    fun openStaticInventory(holder: Player, title: Component, provider: GuiProvider, forceSyncOpening: Boolean)

    fun cacheInventory(holder: Player, title: Component, provider: GuiProvider)

    fun cacheInventory(
        holder: Player,
        title: Component,
        provider: GuiProvider,
        staticInventory: Boolean
    ): GuiInventory?

    fun updateCachedInventory(holder: Player, inventoryId: String)

    fun clearCachedInventories(holder: Player)

    fun removeCachedInventory(holder: Player, inventory: GuiInventory)

    fun getInventory(holder: Player, name: String): GuiInventory?

}

