package net.spacetivity.inventorylib.api

class GuiProvider {

    companion object {
        fun register(api: GuiApi) {
            Companion.api = api
        }

        @JvmStatic
        lateinit var api: GuiApi
            private set
    }

}


