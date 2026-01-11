# Inventory Library API Dokumentation

Eine umfassende Kotlin-Bibliothek für die Erstellung von GUI-Inventaren in Bukkit/Spigot/Paper Minecraft-Servern.

## Inhaltsverzeichnis

- [Übersicht](#übersicht)
- [Installation](#installation)
- [Schnellstart](#schnellstart)
- [Kernkonzepte](#kernkonzepte)
- [API Referenz](#api-referenz)
  - [GuiInventoryProvider](#guiinventoryprovider)
  - [GuiHandler](#guihandler)
  - [GuiInventory](#guiinventory)
  - [GuiController](#guicontroller)
  - [GuiProvider](#guiprovider)
  - [GuiItem](#guiitem)
  - [GuiPagination](#guipagination)
  - [Extension Functions](#extension-functions)
- [Beispiele](#beispiele)
- [Best Practices](#best-practices)

## Übersicht

Die Inventory Library API bietet eine moderne, typsichere und flexible Lösung zur Erstellung von GUI-Inventaren in Minecraft-Plugins. Die Bibliothek unterstützt:

- Statische und dynamische Inventare
- Inventar-Caching für bessere Performance
- Pagination (Seitennavigation)
- Item-Aktionen mit Cooldown-Schutz
- Flexible Positionierung von Items
- Item-Modifikationen zur Laufzeit
- Bestätigungsdialoge
- Navigation zwischen Inventaren

## Installation

Die Bibliothek wird als Gradle-Dependency über GitHub Packages bereitgestellt. Füge das Repository und die Dependency zu deinem `build.gradle.kts` hinzu:

```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/Spacetivity/SpaceInventories")
        // Keine Authentifizierung nötig für öffentliche Packages
    }
    mavenCentral()
}

dependencies {
    implementation("net.spacetivity.inventorylib:inventory-api:1.0-SNAPSHOT")
}
```

**Hinweis:** Da dieses Package öffentlich ist, ist keine Authentifizierung beim Lesen erforderlich.

## Schnellstart

### Einfaches Inventar erstellen

```kotlin
import net.spacetivity.inventorylib.api.extension.openStaticInventory
import net.spacetivity.inventorylib.api.inventory.GuiProvider
import net.spacetivity.inventorylib.api.inventory.GuiProperties
import net.spacetivity.inventorylib.api.inventory.GuiController
import net.spacetivity.inventorylib.api.item.GuiItem
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@GuiProperties(
    id = "my-inventory",
    rows = 3,
    columns = 9
)
class MyInventoryProvider : GuiProvider {
    override fun init(player: Player, controller: GuiController) {
        val item = GuiItem.of(ItemStack(Material.DIAMOND)) { pos, guiItem, event ->
            player.sendMessage("Diamant wurde geklickt!")
        }
        controller.setItem(1, 1, item)
    }
}

val provider = MyInventoryProvider()
openStaticInventory(player, Component.text("Mein Inventar"), provider)
```

## Kernkonzepte

### GuiProvider

Ein `GuiProvider` ist eine Schnittstelle, die die Initialisierung eines Inventars definiert. Sie enthält eine `init`-Methode, die aufgerufen wird, wenn das Inventar erstellt wird.

**Wichtig:** `GuiProvider` muss immer als Klasse implementiert werden (nicht als anonymes Objekt) und sollte mit der `@GuiProperties` Annotation versehen sein.

### GuiController

Der `GuiController` ist das zentrale Steuerungselement für die Verwaltung von Items im Inventar. Er bietet Methoden zum Setzen, Entfernen und Modifizieren von Items.

### GuiItem

Ein `GuiItem` repräsentiert ein Item im Inventar mit einer optionalen Klick-Aktion. Items können zur Laufzeit modifiziert werden.

### GuiInventory

Ein `GuiInventory` repräsentiert ein vollständiges Inventar mit allen seinen Eigenschaften und Funktionen.

## API Referenz

### GuiInventoryProvider

Die zentrale Klasse für den Zugriff auf die API.

```kotlin
object GuiInventoryProvider {
    lateinit var api: GuiApi
    
    fun register(api: GuiApi)
}
```

**Verwendung:**
```kotlin
// API registrieren (wird normalerweise vom Plugin-Framework übernommen)
GuiInventoryProvider.register(apiInstance)

// API-Zugriff
val api = GuiInventoryProvider.api
```

### GuiHandler

Verwaltet alle Inventare für Spieler.

#### Methoden

##### `openStaticInventory`
Öffnet ein statisches Inventar (wird bei jedem Öffnen neu erstellt).

```kotlin
fun openStaticInventory(
    holder: Player,
    title: Component,
    provider: GuiProvider,
    forceSyncOpening: Boolean
)
```

##### `cacheInventory`
Erstellt und cached ein Inventar für einen Spieler.

```kotlin
fun cacheInventory(
    holder: Player,
    title: Component,
    provider: GuiProvider
)

fun cacheInventory(
    holder: Player,
    title: Component,
    provider: GuiProvider,
    staticInventory: Boolean
): GuiInventory?
```

##### `getInventory`
Ruft ein gecachtes Inventar ab.

```kotlin
fun getInventory(holder: Player, name: String): GuiInventory?
```

##### `updateCachedInventory`
Aktualisiert ein gecachtes Inventar.

```kotlin
fun updateCachedInventory(holder: Player, inventoryId: String)
```

##### `removeCachedInventory`
Entfernt ein gecachtes Inventar.

```kotlin
fun removeCachedInventory(holder: Player, inventory: GuiInventory)
```

##### `clearCachedInventories`
Löscht alle gecachten Inventare eines Spielers.

```kotlin
fun clearCachedInventories(holder: Player)
```

### GuiInventory

Repräsentiert ein vollständiges Inventar.

#### Eigenschaften

- `provider: GuiProvider` - Der Provider, der das Inventar initialisiert
- `controller: GuiController` - Der Controller für Item-Verwaltung
- `name: String` - Eindeutiger Name des Inventars
- `title: Component` - Titel des Inventars
- `rows: Int` - Anzahl der Zeilen
- `columns: Int` - Anzahl der Spalten (normalerweise 9)
- `isCloseable: Boolean` - Ob das Inventar geschlossen werden kann
- `isStaticInventory: Boolean` - Ob es ein statisches Inventar ist

#### Methoden

##### `open`
Öffnet das Inventar für einen Spieler.

```kotlin
fun open(holder: Player)
fun open(holder: Player, pageId: Int)
fun open(holder: Player, forceSyncOpening: Boolean)
fun open(holder: Player, pageId: Int, forceSyncOpening: Boolean)
```

##### `close`
Schließt das Inventar für einen Spieler.

```kotlin
fun close(holder: Player)
fun close(holder: Player, forceSyncClosing: Boolean)
```

### GuiController

Zentrale Steuerung für Item-Verwaltung im Inventar.

#### Eigenschaften

- `provider: GuiProvider` - Der zugehörige Provider
- `properties: GuiProperties` - Eigenschaften des Inventars
- `inventorySlotCount: Int` - Gesamtanzahl der Slots
- `isCloseable: Boolean` - Ob das Inventar schließbar ist
- `contents: MutableMap<GuiPos, GuiItem?>` - Alle Items im Inventar
- `pagination: GuiPagination?` - Optional: Pagination-Objekt
- `rawInventory: Inventory?` - Das rohe Bukkit-Inventar

#### Methoden

##### Item-Verwaltung

```kotlin
// Item an Position setzen
fun setItem(pos: GuiPos, item: GuiItem)
fun setItem(row: Int, column: Int, item: GuiItem)

// Item hinzufügen
fun addItem(item: GuiItem)
fun addItemToRandomPosition(item: GuiItem)

// Item entfernen
fun removeItem(name: String)
fun removeItem(type: Material)

// Platzhalter setzen
fun placeholder(pos: GuiPos, type: Material)
fun placeholder(row: Int, column: Int, type: Material)
```

##### Füll-Methoden

```kotlin
fun fill(fillType: FillType, item: GuiItem, vararg positions: GuiPos)
fun clearPosition(pos: GuiPos)
```

**FillType Enum:**
- `ROW` - Füllt eine ganze Zeile
- `RECTANGLE` - Füllt ein Rechteck
- `LEFT_BORDER` - Linker Rand
- `RIGHT_BORDER` - Rechter Rand
- `TOP_BORDER` - Oberer Rand
- `BOTTOM_BORDER` - Unterer Rand
- `ALL_BORDERS` - Alle Ränder

##### Abfrage-Methoden

```kotlin
fun isPositionTaken(pos: GuiPos): Boolean
fun getPositionOfItem(item: GuiItem): GuiPos?
fun getFirstEmptyPosition(): GuiPos?
fun getItem(pos: GuiPos): GuiItem?
fun getItem(row: Int, column: Int): GuiItem?
fun findFirstItemWithType(type: Material): GuiItem?
```

##### Pagination

```kotlin
fun createPagination(): GuiPagination
```

##### Inventar-Dimensionen

```kotlin
fun getInventoryId(): String
fun getRows(): Int
fun getColumns(): Int
```

### GuiProvider

Schnittstelle für die Initialisierung von Inventaren.

```kotlin
interface GuiProvider {
    fun init(player: Player, controller: GuiController)
}
```

**Beispiel:**
```kotlin
@GuiProperties(
    id = "my-inventory",
    rows = 3,
    columns = 9
)
class MyInventoryProvider : GuiProvider {
    override fun init(player: Player, controller: GuiController) {
        // Inventar-Logik hier
    }
}
```

### GuiItem

Repräsentiert ein Item im Inventar mit optionaler Aktion.

#### Erstellung

```kotlin
// Item ohne Aktion
GuiItem.of(itemStack)

// Item mit Aktion
GuiItem.of(itemStack) { pos, guiItem, event ->
    // Aktion ausführen
}

// Platzhalter
GuiItem.placeholder(Material.GRAY_STAINED_GLASS_PANE)

// Navigation-Item
GuiItem.navigator(itemStack, "inventory-key")

// Pagination-Items
GuiItem.nextPage(itemStack, pagination)
GuiItem.previousPage(itemStack, pagination)
```

#### Item-Modifikation

Items können zur Laufzeit modifiziert werden:

```kotlin
guiItem.update(controller, GuiItem.Modification.TYPE, Material.DIAMOND)
guiItem.update(controller, GuiItem.Modification.DISPLAY_NAME, Component.text("Neuer Name"))
guiItem.update(controller, GuiItem.Modification.LORE, mutableListOf(Component.text("Lore")))
guiItem.update(controller, GuiItem.Modification.AMOUNT, 5)
guiItem.update(controller, GuiItem.Modification.INCREMENT, 1)
guiItem.update(controller, GuiItem.Modification.ENCHANTMENTS, ItemEnchantment.of(...))
guiItem.update(controller, GuiItem.Modification.GLOWING, true)
```

**Modification Enum:**
- `TYPE` - Material ändern
- `DISPLAY_NAME` - Anzeigename ändern
- `LORE` - Lore ändern
- `AMOUNT` - Anzahl setzen
- `INCREMENT` - Anzahl erhöhen
- `ENCHANTMENTS` - Verzauberungen ändern
- `GLOWING` - Glow-Effekt ein/aus

#### Cooldown

Items haben standardmäßig einen Cooldown von 250ms, um versehentliche Doppelklicks zu verhindern.

### GuiPagination

Verwaltet die Seitennavigation für große Item-Listen.

#### Eigenschaften

- `positions: List<Any>` - Verfügbare Positionen für Items
- `items: Multimap<Int, GuiItem>` - Items pro Seite
- `getPaginationItems(): List<GuiItem>` - Alle Pagination-Items

#### Methoden

```kotlin
// Seiten-Informationen
fun getLastPageId(): Int
fun getPageAmount(): Int
fun getCurrentPageId(): Int
fun isFirstPage(): Boolean
fun isLastPage(): Boolean

// Navigation
fun page(pageId: Int)
fun toFirstPage()
fun toLastPage()
fun toNextPage()
fun toPreviousPage()

// Konfiguration
fun setItemField(startRow: Int, startColumn: Int, endRow: Int, endColumn: Int)
fun distributeItems(items: List<GuiItem>)
fun limitItemsPerPage(amount: Int)
fun refreshPage()
```

### Extension Functions

Die Bibliothek bietet praktische Extension-Funktionen für einfacheren Zugriff:

```kotlin
// Statisches Inventar öffnen
openStaticInventory(player, title, provider)

// Inventar öffnen (aus Cache)
openInventory(player, "inventory-key")

// Inventar aus Cache abrufen
getInventory(player, "inventory-key")

// Inventar cachen
cacheInventory(player, title, provider)

// Gecachtes Inventar entfernen
removeCachedInventory(player, inventory)

// Alle gecachten Inventare löschen
clearCachedInventories(player)

// Gecachtes Inventar aktualisieren
updateCachedInventory(player, "inventory-key")
```

### GuiApi

Haupt-API-Interface mit zusätzlichen Funktionen.

```kotlin
interface GuiApi {
    val inventoryHandler: GuiHandler
    
    fun openConfirmationInventory(
        holder: Player,
        title: Component,
        displayItem: ItemStack,
        onAccept: ((ItemStack) -> Unit),
        onDeny: ((ItemStack) -> Unit)
    )
}
```

### GuiProperties Annotation

Annotation zur Konfiguration von Inventaren.

```kotlin
@GuiProperties(
    id = "my-inventory",
    rows = 3,
    columns = 9,
    permission = "plugin.use",
    closeable = true,
    playSoundOnClick = true,
    playSoundOnOpen = true,
    playSoundOnClose = true,
    playSoundOnPageSwitch = true
)
class MyInventoryProvider : GuiProvider {
    // ...
}
```

### GuiPos

Repräsentiert eine Position im Inventar (Zeile, Spalte).

```kotlin
data class GuiPos(val row: Int, val column: Int)

// Erstellung
GuiPos.of(row, column)
GuiPos(row, column)
```

### ItemEnchantment

Hilfsklasse für Verzauberungen.

```kotlin
data class ItemEnchantment(
    val enchantment: Enchantment,
    val strength: Int,
    val isActive: Boolean
)

// Erstellung
ItemEnchantment.of(enchantment, strength, isActive)
```

## Beispiele

### Beispiel 1: Einfaches Menü

```kotlin
@GuiProperties(
    id = "main-menu",
    rows = 3,
    columns = 9
)
class MainMenuProvider : GuiProvider {
    override fun init(player: Player, controller: GuiController) {
        // Header
        val header = GuiItem.placeholder(Material.BLUE_STAINED_GLASS_PANE)
        controller.fill(GuiController.FillType.TOP_BORDER, header)
        
        // Items
        val shopItem = GuiItem.of(ItemStack(Material.EMERALD)) { _, _, _ ->
            player.sendMessage("Shop geöffnet!")
            // Shop öffnen...
        }
        controller.setItem(2, 2, shopItem)
        
        val settingsItem = GuiItem.of(ItemStack(Material.REDSTONE)) { _, _, _ ->
            player.sendMessage("Einstellungen geöffnet!")
            // Einstellungen öffnen...
        }
        controller.setItem(2, 6, settingsItem)
    }
}

val menuProvider = MainMenuProvider()
openStaticInventory(player, Component.text("Hauptmenü"), menuProvider)
```

### Beispiel 2: Inventar mit Pagination

```kotlin
@GuiProperties(
    id = "item-list",
    rows = 5,
    columns = 9
)
class ItemListProvider : GuiProvider {
    override fun init(player: Player, controller: GuiController) {
        val pagination = controller.createPagination()
        pagination.setItemField(1, 1, 3, 7) // Items in diesem Bereich
        pagination.limitItemsPerPage(21)
        
        // Items erstellen
        val items = (1..50).map { index ->
            GuiItem.of(ItemStack(Material.DIAMOND, index)) { _, _, _ ->
                player.sendMessage("Item $index geklickt!")
            }
        }
        
        pagination.distributeItems(items)
        
        // Navigation-Buttons
        if (!pagination.isFirstPage()) {
            val prevButton = GuiItem.previousPage(
                ItemStack(Material.ARROW),
                pagination
            )
            controller.setItem(4, 1, prevButton)
        }
        
        if (!pagination.isLastPage()) {
            val nextButton = GuiItem.nextPage(
                ItemStack(Material.ARROW),
                pagination
            )
            controller.setItem(4, 9, nextButton)
        }
    }
}

val listProvider = ItemListProvider()
openStaticInventory(player, Component.text("Item-Liste"), listProvider)
```

### Beispiel 3: Gecachtes Inventar

```kotlin
@GuiProperties(
    id = "cached-inventory",
    rows = 3,
    columns = 9
)
class CachedInventoryProvider : GuiProvider {
    override fun init(player: Player, controller: GuiController) {
        val item = GuiItem.of(ItemStack(Material.DIAMOND)) { _, guiItem, _ ->
            // Item zur Laufzeit modifizieren
            guiItem.update(controller, GuiItem.Modification.AMOUNT, 
                (guiItem.item.amount + 1).coerceAtMost(64))
        }
        controller.setItem(2, 5, item)
    }
}

// Inventar erstellen und cachen
val cachedProvider = CachedInventoryProvider()
cacheInventory(player, Component.text("Gecachtes Inventar"), cachedProvider)

// Später öffnen
openInventory(player, "cached-inventory")

// Aktualisieren
updateCachedInventory(player, "cached-inventory")
```

### Beispiel 4: Bestätigungsdialog

```kotlin
GuiInventoryProvider.api.openConfirmationInventory(
    player,
    Component.text("Löschen bestätigen"),
    ItemStack(Material.TNT),
    onAccept = { item ->
        player.sendMessage("Gelöscht!")
        // Lösch-Logik
    },
    onDeny = { item ->
        player.sendMessage("Abgebrochen!")
    }
)
```

### Beispiel 5: Navigation zwischen Inventaren

```kotlin
// Hauptmenü
@GuiProperties(
    id = "main-menu",
    rows = 3,
    columns = 9
)
class MainMenuProvider : GuiProvider {
    override fun init(player: Player, controller: GuiController) {
        val shopButton = GuiItem.navigator(
            ItemStack(Material.EMERALD),
            "shop-inventory"
        )
        controller.setItem(2, 5, shopButton)
    }
}

val mainMenuProvider = MainMenuProvider()
cacheInventory(player, Component.text("Hauptmenü"), mainMenuProvider)

// Shop-Inventar
@GuiProperties(
    id = "shop-inventory",
    rows = 5,
    columns = 9
)
class ShopProvider : GuiProvider {
    override fun init(player: Player, controller: GuiController) {
        val backButton = GuiItem.navigator(
            ItemStack(Material.ARROW),
            "main-menu"
        )
        controller.setItem(4, 1, backButton)
        
        // Shop-Items...
    }
}

val shopProvider = ShopProvider()
cacheInventory(player, Component.text("Shop"), shopProvider)

// Inventare öffnen
openInventory(player, "main-menu")
```

### Beispiel 6: Item-Modifikation zur Laufzeit

```kotlin
@GuiProperties(
    id = "modification-inventory",
    rows = 3,
    columns = 9
)
class ModificationInventoryProvider : GuiProvider {
    override fun init(player: Player, controller: GuiController) {
        val item = GuiItem.of(ItemStack(Material.DIAMOND)) { _, guiItem, _ ->
            // Verschiedene Modifikationen
            guiItem.update(controller, GuiItem.Modification.AMOUNT, 5)
            guiItem.update(controller, GuiItem.Modification.DISPLAY_NAME, 
                Component.text("Modifiziertes Item"))
            guiItem.update(controller, GuiItem.Modification.GLOWING, true)
            
            val enchantment = ItemEnchantment.of(
                Enchantment.UNBREAKING, 
                1, 
                true
            )
            guiItem.update(controller, GuiItem.Modification.ENCHANTMENTS, enchantment)
        }
        controller.setItem(2, 5, item)
    }
}

val provider = ModificationInventoryProvider()
```

## Best Practices

1. **Statische vs. Gecachte Inventare:**
   - Verwende statische Inventare für einfache, einmalige Menüs
   - Verwende gecachte Inventare für komplexe Inventare, die mehrfach geöffnet werden

2. **Performance:**
   - Cache große Inventare, die häufig geöffnet werden
   - Verwende Pagination für Listen mit vielen Items (>27 Items)

3. **Item-Aktionen:**
   - Nutze den eingebauten Cooldown-Mechanismus
   - Implementiere eigene Validierungen in den Aktionen

4. **Positionierung:**
   - Verwende `GuiPos` für bessere Lesbarkeit
   - Nutze `FillType` für Rahmen und Hintergründe

5. **Fehlerbehandlung:**
   - Prüfe immer, ob ein gecachtes Inventar existiert, bevor du es öffnest
   - Validiere Spieler-Berechtigungen in den Providern

6. **Code-Organisation:**
   - **WICHTIG:** GuiProvider muss immer als Klasse implementiert werden, nicht als anonymes Objekt
   - Verwende immer die `@GuiProperties` Annotation für die Konfiguration
   - Erstelle separate Provider-Klassen für komplexe Inventare
   - Die `id` in `@GuiProperties` muss eindeutig sein und wird für das Caching verwendet

## Lizenz

Diese Bibliothek ist Teil des Spacetivity-Projekts.
