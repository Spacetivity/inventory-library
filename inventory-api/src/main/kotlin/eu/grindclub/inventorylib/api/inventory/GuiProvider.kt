package eu.grindclub.inventorylib.api.inventory

import org.bukkit.entity.Player

interface GuiProvider {

    fun init(player: Player, controller: GuiController)

}

