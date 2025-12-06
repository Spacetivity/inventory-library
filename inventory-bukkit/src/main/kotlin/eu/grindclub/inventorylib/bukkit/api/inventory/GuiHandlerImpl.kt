package eu.grindclub.inventorylib.bukkit.api.inventory

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.kyori.adventure.text.Component
import eu.grindclub.inventorylib.api.inventory.GuiHandler
import eu.grindclub.inventorylib.api.inventory.GuiProperties
import eu.grindclub.inventorylib.api.inventory.Gui
import eu.grindclub.inventorylib.api.inventory.GuiView
import eu.grindclub.inventorylib.api.utils.MathUtils
import eu.grindclub.inventorylib.bukkit.GuiInventoryBukkit
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class GuiHandlerImpl : GuiHandler {

    override val inventories: Multimap<Player, GuiView> = ArrayListMultimap.create()

    companion object {
        private val OPEN_INVENTORY_KEY: NamespacedKey by lazy {
            NamespacedKey(GuiInventoryBukkit.instance, "open-inventory")
        }
    }

    override fun openStaticView(holder: Player, title: Component, provider: Gui, forceSyncOpening: Boolean) {
        val inventory: GuiView = cacheView(holder, title, provider, true) ?: return
        inventory.open(holder, forceSyncOpening)
    }

    override fun cacheView(holder: Player, title: Component, provider: Gui) {
        cacheView(holder, title, provider, false)
    }

    override fun cacheView(holder: Player, title: Component, provider: Gui, staticInventory: Boolean): GuiView? {
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

        val inventory: GuiView = GuiInventoryImpl(provider, title, controller, staticInventory)

        inventories.put(holder, inventory)
        return inventory
    }

    override fun updateCachedView(holder: Player, viewId: String) {
        val inventory: GuiView = getView(holder, viewId) ?: return

        val title: Component = inventory.title
        val provider: Gui = inventory.controller.provider

        inventories[holder].removeIf { inv: GuiView -> inv.name.equals(viewId, true) }
        cacheView(holder, title, provider)

        val elytraView = getView(holder, viewId) ?: return
        val key: String = elytraView.name

        val openInventoryName = holder.persistentDataContainer.get(OPEN_INVENTORY_KEY, PersistentDataType.STRING) ?: return
        if (!key.equals(openInventoryName, true)) return

        elytraView.open(holder, true)
    }

    override fun clearCachedViews(holder: Player) {
        inventories.removeAll(holder)
        if (holder.persistentDataContainer.has(OPEN_INVENTORY_KEY, PersistentDataType.STRING))
            holder.persistentDataContainer.remove(OPEN_INVENTORY_KEY)
    }

    override fun removeCachedView(holder: Player, inventory: GuiView) {
        inventories.remove(holder, inventory)
        if (holder.persistentDataContainer.has(OPEN_INVENTORY_KEY, PersistentDataType.STRING)) {
            holder.persistentDataContainer.remove(OPEN_INVENTORY_KEY)
        }
    }

    override fun getView(holder: Player, name: String): GuiView? {
        return inventories[holder].first { it.name.equals(name, true) }
    }
}

