package top.mrxiaom.mmoi18n.gui.edition;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.version.VersionUtils;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import top.mrxiaom.mmoi18n.Translation;
import top.mrxiaom.mmoi18n.gui.ItemTag;
import top.mrxiaom.mmoi18n.placeholder.TranslatedStat;

import java.util.ArrayList;
import java.util.List;

public class ItemEdition extends EditionInventory {
    private static final int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    private static final NamespacedKey STAT_ID_KEY = new NamespacedKey(MMOItems.plugin, "StatId");

    public ItemEdition(Player player, MMOItemTemplate template) {
        super(player, template);
    }

    @Override
    public String getName() {
        return "编辑物品: " + getEdited().getId();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void arrangeInventory() {
        int min = (page - 1) * slots.length;
        int max = page * slots.length;
        int n = 0;

        /*
         * it has to determine what stats can be applied first because otherwise
         * the for loop will just let some slots empty
         */
        List<ItemStat> availableStats = new ArrayList<>(getEdited().getType().getAvailableStats()).stream()
                .filter(stat -> stat.hasValidMaterial(getCachedItem()) && !(stat instanceof InternalStat))
                .toList();

        for (int j = min; j < Math.min(availableStats.size(), max); j++) {
            ItemStat stat = availableStats.get(j);
            TranslatedStat translated = Translation.getStat(stat);
            ItemStack item = new ItemStack(stat.getDisplayMaterial());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.addItemFlags(ItemFlag.values());
                VersionUtils.addEmptyAttributeModifier(meta);
                meta.setDisplayName(ChatColor.GREEN + translated.name());
                List<String> lore = MythicLib.plugin.parseColors(translated.lore().stream().map(s -> ChatColor.GRAY + s).toList());
                lore.add("");
                if (stat.getCategory() != null) {
                    lore.add(0, "");
                    lore.add(0, ChatColor.BLUE + translated.getLoreTag(stat));
                }

                translated.whenDisplayed(stat, lore, getEventualStatData(stat));

                meta.getPersistentDataContainer().set(STAT_ID_KEY, PersistentDataType.STRING, stat.getId());
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
            inventory.setItem(slots[n++], item);
        }

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) glassMeta.setDisplayName(ChatColor.RED + "- 没有物品状态 -");
        glass.setItemMeta(glassMeta);
        ItemTag.put(glass, "no_item_state");

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta != null) nextMeta.setDisplayName(ChatColor.GREEN + "下一页");
        next.setItemMeta(nextMeta);
        ItemTag.put(next, "next_page");

        ItemStack previous = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previous.getItemMeta();
        if (previousMeta != null) previousMeta.setDisplayName(ChatColor.GREEN + "上一页");
        previous.setItemMeta(previousMeta);
        ItemTag.put(previous, "prev_page");

        while (n < slots.length)
            inventory.setItem(slots[n++], glass);
        inventory.setItem(27, page > 1 ? previous : null);
        inventory.setItem(35, availableStats.size() > max ? next : null);
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public void whenClicked(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getInventory() != event.getClickedInventory())
            return;

        ItemStack item = event.getCurrentItem();
        if (!MMOUtils.isMetaItem(item, false) || event.getInventory().getItem(4) == null)
            return;

        if (ItemTag.has(item, "next_page")) {
            page++;
            refreshInventory();
        }

        if (ItemTag.has(item, "prev_page")) {
            page--;
            refreshInventory();
        }

        ItemMeta meta = item.getItemMeta();
        final String tag = meta == null ? null : meta.getPersistentDataContainer().get(STAT_ID_KEY, PersistentDataType.STRING);
        if (tag == null || tag.isEmpty()) return;

        // Check for OP stats
        final ItemStat edited = MMOItems.plugin.getStats().get(tag);
        if (edited == null) return;
        if (MMOItems.plugin.hasPermissions() && MMOItems.plugin.getLanguage().opStatsEnabled
                && MMOItems.plugin.getLanguage().opStats.contains(edited.getId())
                && !MMOItems.plugin.getVault().getPermissions().has((Player) event.getWhoClicked(), "mmoitems.edit.op")) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "杂鱼没有权限编辑这个状态哦.");
            return;
        }

        edited.whenClicked(this, event);
    }

    @Deprecated
    public ItemEdition onPage(int value) {
        page = value;
        return this;
    }
}