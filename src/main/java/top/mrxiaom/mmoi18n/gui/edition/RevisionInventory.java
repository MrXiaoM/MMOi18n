package top.mrxiaom.mmoi18n.gui.edition;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
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

    public RevisionInventory(@NotNull Player player, @NotNull MMOItemTemplate template) {
        super(player, template);

        // If null
        if (revisionID == null) {

            name = ItemFactory.of(Material.NAME_TAG).name("§3Name").lore(SilentNumbers.chop(
                    "The display name of the old item will be transferred to the new one"
                    , 40, "§7")).build();

            lore = ItemFactory.of(Material.WRITABLE_BOOK).name("§dLore").lore(SilentNumbers.chop(
                    "Specifically keeps lore lines that begin with the color code §n&7"
                    , 40, "§7")).build();

            enchantments = ItemFactory.of(Material.EXPERIENCE_BOTTLE).name("§bEnchantments").lore(SilentNumbers.chop(
                    "This keeps specifically enchantments that are not accounted for in upgrades nor gem stones (presumably added by the player)."
                    , 40, "§7")).build();

            upgrades = ItemFactory.of(Material.NETHER_STAR).name("§aUpgrades").lore(SilentNumbers.chop(
                    "Will this item retain the upgrade level after updating? Only the Upgrade Level is kept (as long as it does not exceed the new max)."
                    , 40, "§7")).build();

            gemstones = ItemFactory.of(Material.EMERALD).name("§eGem Stones").lore(SilentNumbers.chop(
                    "Will the item retain its gem stones when updating? (Note that this allows gemstone overflow - will keep ALL old gemstones even if you reduced the gem sockets)"
                    , 40, "§7")).build();

            soulbind = ItemFactory.of(Material.ENDER_EYE).name("§cSoulbind").lore(SilentNumbers.chop(
                    "If the old item is soulbound, updating will transfer the soulbind to the new item."
                    , 40, "§7")).build();

            external = ItemFactory.of(Material.SPRUCE_SIGN).name("§9External SH").lore(SilentNumbers.chop(
                    "Data registered onto the item's StatHistory by external plugins (like GemStones but not removable)"
                    , 40, "§7")).build();


            // Fill stack
            revisionID = ItemFactory.of(Material.ITEM_FRAME).name("§6Revision ID").lore(SilentNumbers.chop(
                    "The updater is always active, increasing this number will update all instances of this MMOItem without further action."
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
            ItemStack which = null;
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
                    break;
            }

            // If an item corresponds to this slot
            if (which != null) {

                // If it is defined to be enabled
                if (enable != null) {

                    // Add mentioning if enabled
                    inventory.setItem(i, addLore(which, "", "§8Enabled (in config)? §6" + enable.toString()));

                // If ID is enabled
                } else if (id != null) {

                    // Add mentioning if enabled
                    inventory.setItem(i, addLore(which, "", "§8Current Value: §6" + id));

                // Neither enable nor ID are defined
                } else {

                    // Add
                    inventory.setItem(i, which);
                }
            }
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
            event.setCurrentItem(addLore(revisionID.clone(), "", "§8Current Value: §6" + id));
        }
    }

    @NotNull ItemStack addLore(@NotNull ItemStack iSource, String... extraLines){

        // Get its lore
        ArrayList<String> iLore = new ArrayList<>();
        ItemMeta iMeta = iSource.getItemMeta();
        if (iMeta != null && iMeta.getLore() != null) {iLore = new ArrayList<>(iMeta.getLore()); }

        // Add lines
        iLore.addAll(Arrays.asList(extraLines));

        // Put lore
        iMeta.setLore(iLore);

        // Yes
        iSource.setItemMeta(iMeta);

        // Yes
        return iSource;
    }
}
