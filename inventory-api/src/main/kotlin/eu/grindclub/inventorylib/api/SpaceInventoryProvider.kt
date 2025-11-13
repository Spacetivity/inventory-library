package eu.grindclub.inventorylib.api

class SpaceInventoryProvider {

    companion object {
        fun register(api: InventoryApi) {
            Companion.api = api
        }

        @JvmStatic
        lateinit var api: InventoryApi
            private set
    }

}

