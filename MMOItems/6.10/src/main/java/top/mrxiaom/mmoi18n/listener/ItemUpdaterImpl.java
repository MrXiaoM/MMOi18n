package top.mrxiaom.mmoi18n.listener;

import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.mmoi18n.PluginMain;
import top.mrxiaom.mmoi18n.placeholder.TranslatedStat;

public class ItemUpdaterImpl extends ItemUpdater<ItemStat<?, ?>, TranslatedStat> {
    public ItemUpdaterImpl(PluginMain plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void a(PlayerItemHeldEvent event) {
        handle(event);
    }

    /**
     * MMOItems-Dist: net.Indyuce.mmoitems.listener.ItemListener#modifyItem
     */
    @Nullable
    @Override
    protected ItemStack modifyItem(@Nullable ItemStack stack, @NotNull Player player) {

        // Sleep on metaless stacks
        if (stack == null || !stack.hasItemMeta())
            return null;

        // Create a reforger to look at it
        MMOItemReforger mod = new MMOItemReforger(stack);
        if (!mod.hasTemplate())
            return null;

        // Its not GooP Converter's VANILLA is it?
        if ("VANILLA".equals(mod.getNBTItem().getString("MMOITEMS_ITEM_ID")))
            return null;

        // Greater RevID in template? Go ahead, update!
        int templateRevision = mod.getTemplate().getRevisionId();
        int mmoitemRevision = (mod.getNBTItem().hasTag(ItemStats.REVISION_ID.getNBTPath()) ? mod.getNBTItem().getInteger(ItemStats.REVISION_ID.getNBTPath()) : 1);
        if (templateRevision <= mmoitemRevision)
            return null;

        // All right update then
        if (!mod.reforge(MMOItems.plugin.getLanguage().revisionOptions, player))
            return null;

        // Drop all those items
        for (ItemStack drop : player.getInventory().addItem(
                mod.getReforgingOutput().toArray(new ItemStack[0])).values()) {

            // Not air right
            if (SilentNumbers.isAir(drop))
                continue;

            // Drop to the world
            player.getWorld().dropItem(player.getLocation(), drop);
        }

        // That's it
        return mod.getResult();
    }
}
