package eu.grindclub.inventorylib.bukkit.api.inventory

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import eu.grindclub.inventorylib.api.inventory.GuiProvider
import eu.grindclub.inventorylib.api.inventory.GuiInventory
import eu.grindclub.inventorylib.api.pagination.GuiPagination
import eu.grindclub.inventorylib.bukkit.GuiInventoryBukkit
import eu.grindclub.inventorylib.bukkit.utils.SoundUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.metadata.FixedMetadataValue

class GuiInventoryImpl(
    override val provider: GuiProvider,
    override val title: Component,
    override val controller: eu.grindclub.inventorylib.api.inventory.GuiController,
    override val isStaticInventory: Boolean,
) : GuiInventory {

    private val pagination: GuiPagination? = this.controller.pagination
    override val name: String = this.controller.getInventoryId()
    override val rows: Int = this.controller.getRows()
    override val columns: Int = this.controller.getColumns()
    override val isCloseable: Boolean = this.controller.isCloseable

    override fun open(holder: Player) {
        validateOpening(holder)
    }

    override fun open(holder: Player, pageId: Int) {
        if (validateOpening(holder)) return
        if (this.pagination != null) pagination.page(pageId)
    }

    override fun open(holder: Player, forceSyncOpening: Boolean) {
        Bukkit.getScheduler().runTask(GuiInventoryBukkit.instance, Runnable { open(holder) })
    }

    override fun open(holder: Player, pageId: Int, forceSyncOpening: Boolean) {
        Bukkit.getScheduler().runTask(GuiInventoryBukkit.instance, Runnable { open(holder, pageId) })
    }

    override fun close(holder: Player) {
        holder.closeInventory()
    }

    override fun close(holder: Player, forceSyncClosing: Boolean) {
        Bukkit.getScheduler().runTask(GuiInventoryBukkit.instance, Runnable { close(holder) })
    }

    private fun validateOpening(holder: Player): Boolean {
        val permission = controller.properties.permission

        if (!permission.equals("", true) && !holder.hasPermission(permission)) {
            holder.sendMessage(
                MiniMessage.miniMessage().serialize(Component.text(GuiInventoryBukkit.instance.messageFile.noPermissionMessage))
            )
            return true
        }

        val rawInventory: Inventory = controller.rawInventory!!
        holder.openInventory(rawInventory)
        holder.setMetadata("open-inventory", FixedMetadataValue(GuiInventoryBukkit.instance, this.name))
        if (this.controller.properties.playSoundOnOpen) SoundUtils.playOpenSound(holder)

        return false
    }
}

