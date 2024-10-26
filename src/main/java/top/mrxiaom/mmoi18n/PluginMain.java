package top.mrxiaom.mmoi18n;

import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import io.lumine.mythic.lib.UtilityMethods;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.mmoi18n.gui.InjectedInventory;
import top.mrxiaom.mmoi18n.gui.listener.GuiListener;
import top.mrxiaom.mmoi18n.listener.ItemUpdater;
import top.mrxiaom.mmoi18n.placeholder.TranslatedStat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

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
        Translation.init(this);
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
            if (args.length == 1 && "translate".equalsIgnoreCase(args[0])) {
                int count = 0;
                List<ItemStat<?, ?>> stats = MMOItems.plugin.getStats()
                        .getAll().stream()
                        .filter(stat -> !(stat instanceof InternalStat))
                        .toList();
                YamlConfiguration config = new YamlConfiguration();
                for (ItemStat<?, ?> itemStat : stats) {
                    TranslatedStat stat = Translation.getStat(itemStat);
                    if (stat.translated()) continue;

                    String root = "stats." + stat.id();
                    config.set(root + ".name", stat.name());
                    config.set(root + ".lore", stat.lore());
                    String fromCommon = TranslatedStat.getCommonKeyByStat(itemStat);
                    if (fromCommon != null) {
                        config.set(root + ".options.from-common", fromCommon);
                    } else {
                        config.set(root + ".options.lore-stat", Lists.newArrayList("§e► Click to"));
                        config.set(root + ".options.lore-empty", Lists.newArrayList("§e► Click to"));
                    }
                    count++;
                }
                if (count > 0) {
                    try {
                        config.save(new File(getDataFolder(), "stats.untranslated.yml"));
                    } catch (IOException e) {
                        getLogger().log(Level.WARNING, "保存 stats.untranslated.yml 时出现一个异常", e);
                        sender.sendMessage(ChatColor.YELLOW + "保存未翻译属性数据时出现一个异常，详见控制台日志");
                        return true;
                    }
                    sender.sendMessage(ChatColor.GREEN + "发现 " + count + " 个未翻译属性，已保存到 stats.untranslated.yml");
                } else {
                    sender.sendMessage(ChatColor.GRAY + "未找到未翻译属性");
                }
                return true;
            }
            if (args.length == 1 && "unused".equalsIgnoreCase(args[0])) {
                Set<String> keys = Translation.allStats();
                List<ItemStat<?, ?>> stats = MMOItems.plugin.getStats()
                        .getAll().stream()
                        .filter(stat -> !(stat instanceof InternalStat))
                        .toList();
                List<String> unused = keys.stream()
                        .filter(it -> stats.stream().noneMatch(stat -> stat.getId().equals(it)))
                        .toList();
                if (unused.isEmpty()) {
                    sender.sendMessage(ChatColor.GRAY + "未找到未使用的翻译配置");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "未使用的翻译配置如下 (" + unused.size() + "): " + ChatColor.AQUA + String.join(ChatColor.WHITE + ", " + ChatColor.AQUA, unused));
                }
                return true;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender.isOp()) {
            List<String> list = new ArrayList<>();
            if (args.length == 1) {
                String arg = args[0].toLowerCase();
                if ("translate".startsWith(arg)) list.add("translate");
                if ("unused".startsWith(arg)) list.add("unused");
                if ("reload".startsWith(arg)) list.add("reload");
            }
            return list;
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
