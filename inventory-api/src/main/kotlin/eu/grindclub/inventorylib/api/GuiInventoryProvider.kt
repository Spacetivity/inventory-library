package eu.grindclub.inventorylib.api

class GuiInventoryProvider {

    companion object {
        fun register(api: GuiApi) {
            Companion.api = api
        }

        @JvmStatic
        lateinit var api: GuiApi
            private set
    }

}

