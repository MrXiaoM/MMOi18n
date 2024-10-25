package top.mrxiaom.mmoi18n;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import io.lumine.mythic.lib.UtilityMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.mmoi18n.gui.InjectedInventory;
import top.mrxiaom.mmoi18n.gui.listener.GuiListener;
import top.mrxiaom.mmoi18n.listener.ItemUpdater;
import top.mrxiaom.mmoi18n.placeholder.IProvider;

public class PluginMain extends JavaPlugin {
    private ItemUpdater itemUpdater;

    public void onLoad() {
        MinecraftVersion.replaceLogger(getLogger());
        MinecraftVersion.disableUpdateCheck();
        MinecraftVersion.disableBStats();
        MinecraftVersion.getVersion();
    }

    @Override
    public void onEnable() {
        IProvider.createAllProviders(Translation.placeholderProviders);
        Bukkit.getPluginManager().registerEvents(new GuiListener(this), this);
        Bukkit.getPluginManager().registerEvents(itemUpdater = new ItemUpdater(this), this);
        reloadConfig();
    }

    @Override
    public void onDisable() {
        UtilityMethods.closeOpenViewsOfType(InjectedInventory.class);
        HandlerList.unregisterAll(this);
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        super.reloadConfig();
        FileConfiguration config = getConfig();
        config.setDefaults(new YamlConfiguration());

        Translation.reloadConfig(config);
        itemUpdater.reloadConfig();

        getLogger().info("加载了 " + Translation.translatedStatMap.size() + " 个菜单图标翻译配置");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "配置文件已重载");
                return true;
            }
        }
        return true;
    }
}
