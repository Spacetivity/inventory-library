package eu.grindclub.inventorylib.bukkit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import eu.grindclub.inventorylib.api.GuiInventoryProvider
import eu.grindclub.inventorylib.bukkit.api.GuiApiImpl
import eu.grindclub.inventorylib.bukkit.file.MessageFile
import eu.grindclub.inventorylib.bukkit.file.SoundConfigFile
import eu.grindclub.inventorylib.bukkit.listener.GuiPlayerListener
import eu.grindclub.inventorylib.bukkit.utils.FileUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class GuiInventoryBukkit : JavaPlugin() {

    lateinit var soundConfigFile: SoundConfigFile
    lateinit var messageFile: MessageFile

    override fun onEnable() {
        instance = this

        this.soundConfigFile = createOrLoadSoundConfigFile()
        this.messageFile = createOrLoadMessageFile()

        val inventoryApi = GuiApiImpl()
        GuiInventoryProvider.register(inventoryApi)

        GuiPlayerListener(this)

        Bukkit.getConsoleSender().sendMessage(Component.text("Inventory API enabled", NamedTextColor.GREEN))
    }

    private fun createOrLoadSoundConfigFile(): SoundConfigFile {
        return FileUtils.createOrLoadFile(dataFolder.toPath(), "global", "sounds", SoundConfigFile::class, SoundConfigFile())
    }

    private fun createOrLoadMessageFile(): MessageFile {
        return FileUtils.createOrLoadFile(dataFolder.toPath(), "global", "messages", MessageFile::class, MessageFile())
    }

    companion object {
        val GSON: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()

        @JvmStatic
        lateinit var instance: GuiInventoryBukkit
            private set
    }

}

