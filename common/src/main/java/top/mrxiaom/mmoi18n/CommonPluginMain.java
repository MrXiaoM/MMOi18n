package top.mrxiaom.mmoi18n;

import com.google.common.collect.Lists;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.mmoi18n.gui.InjectedInventory;
import top.mrxiaom.mmoi18n.listener.ItemUpdater;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public abstract class CommonPluginMain<ItemStat, TranslatedStat> extends JavaPlugin {
    private ItemUpdater<ItemStat, TranslatedStat> itemUpdater;
    private Translation<ItemStat, TranslatedStat> translation;

    public void onLoad() {
        MinecraftVersion.replaceLogger(getLogger());
        MinecraftVersion.disableUpdateCheck();
        MinecraftVersion.disableBStats();
        MinecraftVersion.getVersion();
    }

    protected abstract Translation<ItemStat, TranslatedStat> createTranslation();
    protected abstract Listener createGuiListener();
    protected abstract ItemUpdater<ItemStat, TranslatedStat> createItemUpdater();
    protected abstract Collection<ItemStat> getAllStats();
    protected abstract boolean isNotInternalStat(ItemStat stat);
    protected abstract void closeOpenViewsOfType(Class<?> type);

    public Translation<ItemStat, TranslatedStat> getTranslation() {
        return translation;
    }

    @Override
    public void onEnable() {
        translation = createTranslation();
        Bukkit.getPluginManager().registerEvents(createGuiListener(), this);
        Bukkit.getPluginManager().registerEvents(itemUpdater = createItemUpdater(), this);
        reloadConfig();
    }

    @Override
    public void onDisable() {
        closeOpenViewsOfType(InjectedInventory.class);
        HandlerList.unregisterAll(this);
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        super.reloadConfig();
        FileConfiguration config = getConfig();
        config.setDefaults(new YamlConfiguration());

        translation.reloadConfig(config);
        itemUpdater.reloadConfig();

        getLogger().info("加载了 " + translation.translatedStatMap.size() + " 个菜单图标翻译配置");
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
                List<ItemStat> stats = getAllStats().stream()
                        .filter(this::isNotInternalStat)
                        .toList();
                YamlConfiguration config = new YamlConfiguration();
                for (ItemStat itemStat : stats) {
                    if (translation.resolveNotTranslated(config, itemStat)) {
                        count++;
                    }
                }
                if (count > 0) {
                    try {
                        config.setComments("stats", Lists.newArrayList(
                                "这个文件使用 /mii translate 命令生成，插件只写不读。",
                                "用于存储导出的当前未翻译的物品属性。",
                                "如需翻译这些属性，请将本文件的各属性复制到 stats.yml 中再进行编辑。"
                        ));
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
                Set<String> keys = translation.allStats();
                List<ItemStat> stats = getAllStats().stream()
                        .filter(this::isNotInternalStat)
                        .toList();
                List<String> unused = keys.stream()
                        .filter(it -> stats.stream().noneMatch(stat -> translation.getItemStatId(stat).equals(it)))
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
