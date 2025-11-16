package eu.grindclub.inventorylib.api.inventory

import org.bukkit.entity.Player

interface Gui {

    fun init(player: Player, controller: GuiController)

}


