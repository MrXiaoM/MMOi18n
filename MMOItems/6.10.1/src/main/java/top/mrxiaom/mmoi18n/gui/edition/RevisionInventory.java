package top.mrxiaom.mmoi18n.gui.edition;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.gui.Navigator;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import net.Indyuce.mmoitems.stat.RevisionID;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.mmoi18n.gui.ItemTag;

import java.util.ArrayList;
import java.util.Arrays;

import static top.mrxiaom.mmoi18n.Translation.translateBoolean;

/**
 * Inventory displayed when enabling the item updater.
 * @see RevisionID
 * @see MMOItemReforger
 *
 * @author Gunging
 */
public class RevisionInventory extends EditionInventory {

    static ItemStack name;
    static ItemStack lore;
    static ItemStack enchantments;
    static ItemStack upgrades;
    static ItemStack gemstones;
    static ItemStack soulbind;
    static ItemStack external;

    static ItemStack revisionID;

    public RevisionInventory(@NotNull Navigator navigator, @NotNull MMOItemTemplate template) {
        super(navigator, template);

        // If null
        if (revisionID == null) {

            name = ItemFactory.of(Material.NAME_TAG).name("§3物品名").lore(SilentNumbers.chop(
                    "旧物品的物品名保留到新物品"
                    , 40, "§7")).build();

            lore = ItemFactory.of(Material.WRITABLE_BOOK).name("§d物品Lore").lore(SilentNumbers.chop(
                    "特别保留以这个颜色代码开头的行: §n&7"
                    , 40, "§7")).build();

            enchantments = ItemFactory.of(Material.EXPERIENCE_BOTTLE).name("§b附魔").lore(SilentNumbers.chop(
                    "保留未被升级或宝石认定的特定附魔 (推测由玩家添加的附魔)."
                    , 40, "§7")).build();

            upgrades = ItemFactory.of(Material.NETHER_STAR).name("§a升级").lore(SilentNumbers.chop(
                    "物品是否会在更新后保留升级等级? 仅会保留等级 (只要不超过新的最大值)."
                    , 40, "§7")).build();

            gemstones = ItemFactory.of(Material.EMERALD).name("§e宝石").lore(SilentNumbers.chop(
                    "物品是否会在更新时保留宝石? (注意，这个选项会允许宝石数量溢出 - 即使你减少了可用宝石槽位，依然会保留物品上的所有旧宝石)"
                    , 40, "§7")).build();

            soulbind = ItemFactory.of(Material.ENDER_EYE).name("§c灵魂绑定").lore(SilentNumbers.chop(
                    "如果旧物品有灵魂绑定, 更新将会把灵魂绑定转移到新物品."
                    , 40, "§7")).build();

            external = ItemFactory.of(Material.SPRUCE_SIGN).name("§9额外状态历史").lore(SilentNumbers.chop(
                    "保留由第三方插件注册到物品的额外状态历史数据 (比如像 GemStones 之类的插件添加的)"
                    , 40, "§7")).build();

            // Fill stack
            revisionID = ItemFactory.of(Material.ITEM_FRAME).name("§6修订版本号").lore(SilentNumbers.chop(
                    "物品更新器始终活跃, 增加这个数字将会被动更新所有 MMOItem 物品实例，无需进一步操作."
                    , 40, "§7")).build();
            ItemTag.put(revisionID, "revision_button");
        }
    }

    @Override
    public String getName() {
        return "修订版本管理";
    }

    @Override
    public void arrangeInventory() {
        // Place corresponding item stacks in there
        for (int i = 0; i < inventory.getSize(); i++) {
            // What item to even put here
            ItemStack which;
            Boolean enable = null;
            Integer id = null;
            switch (i) {
                case 19:
                    which = name.clone();
                    enable = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepName();
                    break;
                case 20:
                    which = lore.clone();
                    enable = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepLore();
                    break;
                case 21:
                    which = enchantments.clone();
                    enable = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepEnchantments();
                    break;
                case 22:
                    which = external.clone();
                    enable = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepExternalSH();
                    break;
                case 28:
                    which = upgrades.clone();
                    enable = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepUpgrades();
                    break;
                case 29:
                    which = gemstones.clone();
                    enable = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepGemStones();
                    break;
                case 30:
                    which = soulbind.clone();
                    enable = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepSoulBind();
                    break;
                case 33:
                    id = getEditedSection().getInt(ItemStats.REVISION_ID.getPath(), 1);
                    which = revisionID.clone();
                    break;
                default:
                    continue;
            }
            // If an item corresponds to this slot
            // If it is defined to be enabled
            if (enable != null) {
                // Add mentioning if enabled
                inventory.setItem(i, addLore(which, "", "§8是否启用 (在配置文件)? §6" + translateBoolean(enable)));
                continue;
            }
            // If ID is enabled, add mentioning if enabled
            inventory.setItem(i, addLore(which, "", "§8当前值: §6" + id));
        }
    }

    @Override
    public void whenClicked(InventoryClickEvent event) {
        // Get the clicked item
        ItemStack item = event.getCurrentItem();
        event.setCancelled(true);
        // If the click did not happen in the correct inventory, or the item is not clickable
        if (event.getInventory() != event.getClickedInventory() || !MMOUtils.isMetaItem(item, false)) { return; }
        // Is the player clicking the revision ID thing?
        if (ItemTag.has(item, "revision_button")) {
            // Get the current ID
            int id = getEditedSection().getInt(ItemStats.REVISION_ID.getPath(), 1);
            // If right click
            if (event.getAction() == InventoryAction.PICKUP_HALF) {
                // Decrease by 1, but never before 1
                id = Math.max(id - 1, 1);
            // Any other click
            } else {
                // Increase by 1 until the ultimate maximum
                id = Math.min(id + 1, Integer.MAX_VALUE);
            }
            // Register edition
            getEditedSection().set(ItemStats.REVISION_ID.getPath(), id);
            registerTemplateEdition();
            // Update ig
            event.setCurrentItem(addLore(revisionID.clone(), "", "§8当前值: §6" + id));
        }
    }

    @NotNull ItemStack addLore(@NotNull ItemStack iSource, String... extraLines){
        // Get its lore
        ArrayList<String> iLore;
        ItemMeta iMeta = iSource.getItemMeta();
        if (iMeta != null) {
            iLore = iMeta.getLore() == null
                    ? new ArrayList<>()
                    : new ArrayList<>(iMeta.getLore());
            // Add lines
            iLore.addAll(Arrays.asList(extraLines));
            // Put lore
            iMeta.setLore(iLore);
        }
        // Yes
        iSource.setItemMeta(iMeta);
        // Yes
        return iSource;
    }
}
