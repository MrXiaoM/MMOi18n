package top.mrxiaom.mmoi18n.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.mmoi18n.CommonPluginMain;

public abstract class ItemUpdater<ItemStat, TranslatedStat> implements Listener {
    CommonPluginMain<ItemStat, TranslatedStat> plugin;
    boolean enableItemHeld = false;
    public ItemUpdater(CommonPluginMain<ItemStat, TranslatedStat> plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig() {
        FileConfiguration config = plugin.getConfig();
        enableItemHeld = config.getBoolean("revision-item-updater.item-held");
    }

    protected void handle(PlayerItemHeldEvent event) {
        if (!enableItemHeld) return;
        Player player = event.getPlayer();
        int slot = event.getNewSlot();
        PlayerInventory inv = player.getInventory();
        ItemStack itemStack = inv.getItem(slot);
        ItemStack newItem = modifyItem(itemStack, player);
        if (newItem != null) inv.setItem(slot, newItem);
    }

    /**
     * MMOItems-Dist: net.Indyuce.mmoitems.listener.ItemListener#modifyItem
     */
    @Nullable
    protected abstract ItemStack modifyItem(@Nullable ItemStack stack, @NotNull Player player);
}
