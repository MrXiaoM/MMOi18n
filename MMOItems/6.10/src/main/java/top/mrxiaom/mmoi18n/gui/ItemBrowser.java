package top.mrxiaom.mmoi18n.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.util.AdventureUtils;
import io.lumine.mythic.lib.version.VersionUtils;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.stat.BrowserDisplayIDX;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.mmoi18n.PluginMain;
import top.mrxiaom.mmoi18n.edition.NewItemEdition;
import top.mrxiaom.mmoi18n.gui.edition.ItemEdition;
import top.mrxiaom.mmoi18n.language.IHolderAccessor;
import top.mrxiaom.mmoi18n.language.Language;
import top.mrxiaom.mmoi18n.language.LanguageEnumAutoHolder;

import java.util.*;
import java.util.stream.Collectors;

import static top.mrxiaom.mmoi18n.gui.ItemTag.has;
import static top.mrxiaom.mmoi18n.gui.ItemTag.put;
import static top.mrxiaom.mmoi18n.language.LanguageEnumAutoHolder.wrap;

public class ItemBrowser extends net.Indyuce.mmoitems.gui.ItemBrowser implements InjectedInventory {
    @Language(prefix = "gui.item-browser.")
    public enum Msg implements IHolderAccessor {
        TYPE__TITLE("物品浏览器"),
        TYPE__ITEM__DISPLAY("&a%s&8 (点击打开浏览)"),
        TYPE__ITEM__DISPLAY_NONE("&c- 无类型 -"),
        TYPE__ITEM__LORE_NONE(Lists.newArrayList("&7&o该类型&c&o没有&7&o物品")),
        TYPE__ITEM__LORE_ONE(Lists.newArrayList("&7&o该类型有 %d 个物品")),
        TYPE__ITEM__LORE_MORE(Lists.newArrayList("&7&o该类型有 %d 个物品")),
        TYPE__PREV_PAGE("&a上一页"),
        TYPE__NEXT_PAGE("&a下一页"),

        ITEM__TITLE("物品浏览器: %s"),
        ITEM__TITLE_DELETE("删除模式: %s"),
        ITEM__ITEM_ERROR__DISPLAY("&c- 错误 -"),
        ITEM__ITEM_ERROR__LORE("&o尝试生成物品时", "&o出现了一个错误."),
        ITEM__ITEM__DISPLAY_NONE("&c- 没有物品 -"),
        ITEM__ITEM__DISPLAY_DELETE("&c删除 %s"),
        ITEM__ITEM__LORE_DELETE(Lists.newArrayList("&c✖ 点击删除 ✖")),
        ITEM__ITEM__LORE("&e▸ 左键 获取物品.", "&e▸ 右键 编辑."),
        ITEM__PREV_PAGE("&a上一页"),
        ITEM__NEXT_PAGE("&a下一页"),
        ITEM__BACK("&a⇨ 返回"),
        ITEM__CREATE_ITEM("&a创建物品"),
        ITEM__DELETE__ENTRY("&c删除物品"),
        ITEM__DELETE__EXIT("&c退出删除模式"),
        ITEM__DEFAULT_RES__DISPLAY("&a下载默认材质包"),
        ITEM__DEFAULT_RES__LORE("&d只能看到石头方块?", "",
                "&c下载默认材质包让你可以",
                "&c按你所想编辑方块.",
                "&c你依然需要将其添加到你的服务器!"),
        ITEM__DEFAULT_RES__LINK_TEXT("点击这里下载默认材质包"),
        ITEM__DEFAULT_RES__LINK_HOVER("点击通过 Dropbox 下载"),

        ;
        Msg(String defaultValue) {
            holder = wrap(this, defaultValue);
        }
        Msg(String... defaultValue) {
            holder = wrap(this, defaultValue);
        }
        Msg(List<String> defaultValue) {
            holder = wrap(this, defaultValue);
        }
        private final LanguageEnumAutoHolder<Msg> holder;
        public LanguageEnumAutoHolder<Msg> holder() {
            return holder;
        }
    }
    private final Map<String, ItemStack> cached = new LinkedHashMap<>();
    private final List<Type> itemTypes;

    private final Type type;
    private boolean deleteMode;

    // Slots used to display items based on the item type explored
    private static final int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
    private static final int[] slotsAlt = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

    private static final String CUSTOM_RP_DOWNLOAD_LINK = "https://www.dropbox.com/s/90w9pvdbfeyxu94/MICustomBlockPack.zip?dl=1";
    private static final NamespacedKey TYPE_ID_KEY = new NamespacedKey(MMOItems.plugin, "TypeId");
    private final PluginMain plugin;

    public ItemBrowser(PluginMain plugin, Player player) {
        this(plugin, player, null);
    }

    public ItemBrowser(PluginMain plugin, Player player, Type type) {
        super(player);
        this.plugin = plugin;
        this.type = type;
        this.itemTypes = MMOItems.plugin.getTypes()
                .getAll().stream()
                .filter(Type::isDisplayed)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        /*
         * ------------------------------
         *          TYPE BROWSER
         *
         *          Displays all possible item types if no type was previously selected by the player.
         * ------------------------------
         */
        if (type == null) {
            int[] usedSlots = slots;
            int min = (page - 1) * usedSlots.length;
            int max = page * usedSlots.length;
            int n = 0;
            // Create inventory
            Inventory inv = Bukkit.createInventory(this, 54, Msg.TYPE__TITLE.str());
            // Fetch the list of types
            for (int j = min; j < Math.min(max, itemTypes.size()); j++) {
                // Current type to display into the GUI
                Type currentType = itemTypes.get(j);
                // Get number of items
                int items = MMOItems.plugin.getTemplates().getTemplates(currentType).size();
                // Display how many items are in the type
                final ItemStack item = currentType.getItem();
                item.setAmount(Math.max(1, Math.min(64, items)));
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (MythicLib.plugin.getVersion().isAbove(1, 20, 5)) VersionUtils.addEmptyAttributeModifier(meta);
                    String translated = plugin.getTranslation().translateType(currentType.getId());
                    AdventureUtils.setDisplayName(meta, Msg.TYPE__ITEM__DISPLAY.str(translated == null ? currentType.getName() : translated));
                    meta.addItemFlags(ItemFlag.values());
                    List<String> lore = new ArrayList<>();
                    if (translated == null) {
                        lore.add("&8[ missing translation: " + currentType.getId() + " ]");
                    }

                    lore.addAll((items < 1 ? Msg.TYPE__ITEM__LORE_NONE
                            : items == 1 ? Msg.TYPE__ITEM__LORE_ONE
                            : Msg.TYPE__ITEM__LORE_MORE).list(items));
                    AdventureUtils.setLore(meta, lore);
                    meta.getPersistentDataContainer().set(TYPE_ID_KEY, PersistentDataType.STRING, currentType.getId());
                }
                item.setItemMeta(meta);
                // Set item
                inv.setItem(slots[n++], item);
            }

            // Fill remainder slots with 'No Type' notice
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta glassMeta = glass.getItemMeta();
            if (glassMeta != null) AdventureUtils.setDisplayName(glassMeta, Msg.TYPE__ITEM__DISPLAY_NONE.str());
            glass.setItemMeta(glassMeta);
            put(glass, "browser_no_type");

            // Next Page
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            if (nextMeta != null) AdventureUtils.setDisplayName(nextMeta, Msg.TYPE__NEXT_PAGE.str());
            next.setItemMeta(nextMeta);
            put(next, "next_page");

            // Previous Page
            ItemStack previous = new ItemStack(Material.ARROW);
            ItemMeta previousMeta = previous.getItemMeta();
            if (previousMeta != null) AdventureUtils.setDisplayName(previousMeta, Msg.TYPE__PREV_PAGE.str());
            previous.setItemMeta(previousMeta);
            put(previous, "prev_page");

            // Fill
            while (n < slots.length) {
                inv.setItem(slots[n++], glass);
            }
            inv.setItem(18, page > 1 ? previous : null);
            inv.setItem(26, max >= MMOItems.plugin.getTypes().getAll().size() ? null : next);

            // Done
            return inv;
        }
        /*
         * ------------------------------
         *          ITEM BROWSER
         *
         *          Displays all the items of the chosen Type
         *  ------------------------------
         */
        String typeName;
        {
            String translated = plugin.getTranslation().translateType(type.getId());
            typeName = translated == null
                    ? MythicLib.plugin.getAdventureParser().stripColors(type.getName())
                    : translated;
        }
        Inventory inv = Bukkit.createInventory(this, 54,
                (deleteMode ? Msg.ITEM__TITLE_DELETE : Msg.ITEM__TITLE).str(typeName));
        /*
         * Build cool Item Stacks for buttons and sh
         */
        ItemStack error = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta errorMeta = error.getItemMeta();
        if (errorMeta != null) {
            AdventureUtils.setDisplayName(errorMeta, Msg.ITEM__ITEM_ERROR__DISPLAY.str());
            AdventureUtils.setLore(errorMeta, Msg.ITEM__ITEM_ERROR__LORE.list());
        }
        error.setItemMeta(errorMeta);

        ItemStack noItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta noItemMeta = noItem.getItemMeta();
        if (noItemMeta != null) AdventureUtils.setDisplayName(noItemMeta, Msg.ITEM__ITEM__DISPLAY_NONE.str());
        noItem.setItemMeta(noItemMeta);

        ItemStack previous = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previous.getItemMeta();
        if (previousMeta != null) AdventureUtils.setDisplayName(previousMeta, Msg.ITEM__PREV_PAGE.str());
        previous.setItemMeta(previousMeta);
        put(previous, "prev_page");

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta != null) AdventureUtils.setDisplayName(nextMeta, Msg.ITEM__NEXT_PAGE.str());
        next.setItemMeta(nextMeta);
        put(next, "next_page");

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) AdventureUtils.setDisplayName(backMeta, Msg.ITEM__BACK.str());
        back.setItemMeta(backMeta);
        put(back, "back");

        ItemStack create = new ItemStack(new ItemStack(Material.WRITABLE_BOOK));
        ItemMeta createMeta = create.getItemMeta();
        if (createMeta != null) AdventureUtils.setDisplayName(createMeta, Msg.ITEM__CREATE_ITEM.str());
        create.setItemMeta(createMeta);
        put(create, "create_new");

        ItemStack delete = new ItemStack(new ItemStack(Material.CAULDRON));
        ItemMeta deleteMeta = delete.getItemMeta();
        if (deleteMeta != null) AdventureUtils.setDisplayName(deleteMeta, (deleteMode ? Msg.ITEM__DELETE__EXIT : Msg.ITEM__DELETE__ENTRY).str());
        delete.setItemMeta(deleteMeta);
        put(delete, deleteMode ? "cancel_deletion" : "delete_item");

        if (type == Type.BLOCK) {
            ItemStack downloadPack = new ItemStack(Material.HOPPER);
            ItemMeta downloadMeta = downloadPack.getItemMeta();
            if (downloadMeta != null) {
                AdventureUtils.setDisplayName(downloadMeta, Msg.ITEM__DEFAULT_RES__DISPLAY.str());
                AdventureUtils.setLore(downloadMeta, Msg.ITEM__DEFAULT_RES__LORE.list());
            }
            downloadPack.setItemMeta(downloadMeta);
            put(downloadPack, "download_default_resourcepack");
            inv.setItem(45, downloadPack);
        }
        // Get templates of this type
        HashMap<Double, ArrayList<MMOItemTemplate>> templates = BrowserDisplayIDX.select(MMOItems.plugin.getTemplates().getTemplates(type));
        /*
         *  -----------
         *    CALCULATE GUI BOUNDS AND PAGE SIZES
         *
         *      Each display index claims the entire column of items, such that there will be
         *      empty spaces added to fill the inventories.
         *
         *      In Four GUI mode, columns are four slots tall, else they are three slots tall.
         *  -----------
         */
        int[] usedSlots = type.isFourGUIMode() ? slotsAlt : slots;
        int min = (page - 1) * usedSlots.length;
        int max = page * usedSlots.length;
        int n = 0;
        int sc = type.isFourGUIMode() ? 4 : 3;
        int totalSpaceCount = 0;
        for (Map.Entry<Double, ArrayList<MMOItemTemplate>> indexTemplates : templates.entrySet()) {
            // Claim columns
            int totalSpaceAdd = indexTemplates.getValue().size();
            while (totalSpaceAdd > 0) {
                totalSpaceCount += sc;
                totalSpaceAdd -= sc;
            }
        }
        /*
         * Over the page-range currently in use...
         */
        for (int j = min; j < Math.min(max, totalSpaceCount); j++) {
            MMOItemTemplate template = BrowserDisplayIDX.getAt(j, templates);
            // No template here?
            if (template == null) {
                // Set Item
                inv.setItem(usedSlots[n], noItem);
                /*
                 *      Calculate next n from the slots.
                 *
                 *      #1 Adding 7 will give you the slot immediately under
                 *
                 *      #2 If it overflows, subtract 7sc (column space * 7)
                 *         and add one
                 */
                n += 7;
                if (n >= usedSlots.length) {
                    n -= 7 * sc;
                    n++;
                }
                continue;
            }
            // Build item -> any errors?
            final ItemStack item = template.newBuilder(playerData.getRPG(), true).build().newBuilder().build();
            if (item == null || item.getType().isAir() || !item.getType().isItem() || item.getItemMeta() == null) {
                // Set Item
                cached.put(template.getId(), error);
                inv.setItem(usedSlots[n], error);
                /*
                 *      Calculate next n from the slots.
                 *
                 *      #1 Adding 7 will give you the slot immediately under
                 *
                 *      #2 If it overflows, subtract 7sc (column space * 7)
                 *         and add one
                 */
                n += 7;
                if (n >= usedSlots.length) {
                    n -= 7 * sc;
                    n++;
                }
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add("");
            // Deleting lore?
            if (deleteMode) {
                String name = (meta.hasDisplayName() ? meta.getDisplayName() : MMOUtils.getDisplayName(item));
                AdventureUtils.setDisplayName(meta, Msg.ITEM__ITEM__DISPLAY_DELETE.str(name));
                lore.addAll(Msg.ITEM__ITEM__LORE_DELETE.list());
                // Editing lore?
            } else {
                lore.addAll(Msg.ITEM__ITEM__LORE.list());
            }
            AdventureUtils.setLore(meta, lore);
            item.setItemMeta(meta);
            // Set item
            cached.put(template.getId(), item);
            inv.setItem(usedSlots[n], cached.get(template.getId()));

            /*
             *      Calculate next n from the slots.
             *
             *      #1 Adding 7 will give you the slot immediately under
             *
             *      #2 If it overflows, subtract 7sc (column space * 7)
             *         and add one
             */
            n += 7;
            if (n >= usedSlots.length) {
                n -= 7 * sc;
                n++;
            }
        }
        // Put the buttons
        if (!deleteMode) {
            inv.setItem(51, create);
        }
        inv.setItem(47, delete);
        inv.setItem(49, back);
        inv.setItem(18, page > 1 ? previous : null);
        inv.setItem(26, max >= totalSpaceCount ? null : next);
        for (int i : usedSlots) {
            if (SilentNumbers.isAir(inv.getItem(i))) {
                inv.setItem(i, noItem);
            }
        }
        return inv;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void whenClicked(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getInventory() != event.getClickedInventory())
            return;

        ItemStack item = event.getCurrentItem();
        if (MMOUtils.isMetaItem(item, false)) {
            if (has(item, "next_page")) {
                page++;
                open();
            } else if (has(item, "prev_page")) {
                page--;
                open();
            } else if (has(item, "back")) {
                new ItemBrowser(plugin, getPlayer()).open();
            } else if (has(item, "cancel_deletion")) {
                deleteMode = false;
                open();
            } else if (has(item, "create_new")) {
                new NewItemEdition(this).enable();
            } else if (type != null && has(item, "delete_item")) {
                deleteMode = true;
                open();
            } else if (has(item, "download_default_resourcepack")) {
                JsonArray json = new JsonArray();
                JsonObject obj = new JsonObject();
                {
                    obj.addProperty("text", Msg.ITEM__DEFAULT_RES__LINK_TEXT.str());
                    obj.addProperty("color", "green");
                    JsonObject click = new JsonObject();
                    {
                        click.addProperty("action", "open_url");
                        click.addProperty("value", CUSTOM_RP_DOWNLOAD_LINK);
                        obj.add("clickEvent", click);
                    }
                    JsonObject hover = new JsonObject();
                    {
                        hover.addProperty("action", "show_text");
                        JsonArray hoverValue = new JsonArray();
                        {
                            JsonObject hoverText = new JsonObject();
                            {
                                hoverText.addProperty("text", Msg.ITEM__DEFAULT_RES__LINK_HOVER.str());
                                hoverText.addProperty("italic", true);
                                hoverText.addProperty("color", "white");
                                hoverValue.add("");
                                hoverValue.add(hoverText);
                            }
                            hover.add("value", hoverValue);
                        }
                        obj.add("hoverEvent", hover);
                    }
                }
                json.add(obj);

                MythicLib.plugin.getVersion().getWrapper().sendJson(getPlayer(), json.toString());
                getPlayer().closeInventory();
            } else if (type == null && !has(item, "browser_no_type")) {
                ItemMeta meta = item.getItemMeta();
                final String typeId = meta == null ? null : meta.getPersistentDataContainer().get(TYPE_ID_KEY, PersistentDataType.STRING);
                new ItemBrowser(plugin, getPlayer(), MMOItems.plugin.getTypes().get(typeId)).open();
            }
        }

        String id = NBTItem.get(item).getString("MMOITEMS_ITEM_ID");
        if (type == null || id.isEmpty()) return;

        if (deleteMode) {
            MMOItems.plugin.getTemplates().deleteTemplate(type, id);
            deleteMode = false;
            open();
        } else {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                getPlayer().getInventory().addItem(MMOItems.plugin.getItem(type, id, playerData));
                getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
            }
            if (event.getAction() == InventoryAction.PICKUP_HALF) {
                new ItemEdition(plugin, getPlayer(), MMOItems.plugin.getTemplates().getTemplate(type, id)).open();
            }
        }
    }
}
