package eu.grindclub.inventorylib.bukkit.api.inventory

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.inventory.GuiHandler
import eu.grindclub.inventorylib.api.inventory.GuiProperties
import eu.grindclub.inventorylib.api.inventory.GuiProvider
import eu.grindclub.inventorylib.api.inventory.GuiInventory
import eu.grindclub.inventorylib.api.utils.MathUtils
import eu.grindclub.inventorylib.bukkit.GuiInventoryBukkit
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class GuiHandlerImpl : GuiHandler {

    override val inventories: Multimap<Player, GuiInventory> = ArrayListMultimap.create()

    override fun openStaticInventory(holder: Player, title: Component, provider: GuiProvider, forceSyncOpening: Boolean) {
        val inventory: GuiInventory = cacheInventory(holder, title, provider, true) ?: return
        inventory.open(holder, forceSyncOpening)
    }

    override fun cacheInventory(holder: Player, title: Component, provider: GuiProvider) {
        cacheInventory(holder, title, provider, false)
    }

    override fun cacheInventory(holder: Player, title: Component, provider: GuiProvider, staticInventory: Boolean): GuiInventory? {
        val permission = if (provider.javaClass.getAnnotation(GuiProperties::class.java) == null) ""
        else provider.javaClass.getAnnotation(GuiProperties::class.java).permission

        if (!permission.equals("", true) && !holder.hasPermission(permission)) return null

        val controller: eu.grindclub.inventorylib.api.inventory.GuiController = GuiControllerImpl(provider)
        val rawInventory = Bukkit.createInventory(holder, controller.getRows() * controller.getColumns(), title)

        controller.rawInventory = rawInventory

        for (i in 0 until controller.inventorySlotCount) {
            controller.contents[MathUtils.slotToPosition(i, controller.getColumns())] = null
        }

        provider.init(holder, controller)

        controller.updateRawInventory()

        val inventory: GuiInventory = GuiInventoryImpl(provider, title, controller, staticInventory)

        inventories.put(holder, inventory)
        return inventory
    }

    override fun updateCachedInventory(holder: Player, inventoryId: String) {
        val inventory: GuiInventory = getInventory(holder, inventoryId) ?: return

        val title: Component = inventory.title
        val provider: GuiProvider = inventory.controller.provider

        inventories[holder].removeIf { inv: GuiInventory -> inv.name.equals(inventoryId, true) }
        cacheInventory(holder, title, provider)

        val elytraInventory = getInventory(holder, inventoryId) ?: return
        val key: String = elytraInventory.name

        if (!holder.hasMetadata("open-inventory")) return
        if (!key.equals(holder.getMetadata("open-inventory")[0].value() as String, true)) return

        elytraInventory.open(holder, true)
    }

    override fun clearCachedInventories(holder: Player) {
        inventories.removeAll(holder)
        if (holder.hasMetadata("open-inventory"))
            holder.removeMetadata("open-inventory", GuiInventoryBukkit.instance)
    }

    override fun removeCachedInventory(holder: Player, inventory: GuiInventory) {
        inventories.remove(holder, inventory)
        if (holder.hasMetadata("open-inventory")) {
            holder.removeMetadata("open-inventory", GuiInventoryBukkit.instance)
        }
    }

    override fun getInventory(holder: Player, name: String): GuiInventory? {
        return inventories[holder].first { it.name.equals(name, true) }
    }
}

