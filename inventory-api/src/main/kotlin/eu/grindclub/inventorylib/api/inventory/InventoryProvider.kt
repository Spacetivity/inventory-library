package eu.grindclub.inventorylib.api.inventory

import org.bukkit.entity.Player

interface InventoryProvider {

    fun init(player: Player, controller: InventoryController)

}
