package top.mrxiaom.mmoi18n;

import io.lumine.mythic.lib.UtilityMethods;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.mmoi18n.language.AbstractLanguageHolder;
import top.mrxiaom.mmoi18n.language.LanguageEnumAutoHolder;
import top.mrxiaom.mmoi18n.placeholder.IProvider;
import top.mrxiaom.mmoi18n.placeholder.TranslatedStat;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;

public class Translation {
    protected static final List<IProvider> placeholderProviders = new ArrayList<>();
    protected static final Map<String, TranslatedStat> translatedStatMap = new HashMap<>();
    protected static final Map<String, String> typeTranslation = new HashMap<>();
    protected static final Map<String, List<String>> commonExtraLoreStat = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    protected static final Map<String, List<String>> commonExtraLoreEmpty = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private static final Map<String, Object> holderValues = new HashMap<>();
    private static final Map<String, AbstractLanguageHolder> holders = new HashMap<>();
    private static File file = null;
    private static PluginMain plugin;
    /**
     * 注册枚举到语言管理器
     * @param enumType 枚举类型
     * @param getter 获取 holder 实例的 getter
     */
    protected static <T extends Enum<T>> void register(Class<T> enumType, Function<T, LanguageEnumAutoHolder<T>> getter) {
        for (T value : enumType.getEnumConstants()) {
            LanguageEnumAutoHolder<T> holder = getter.apply(value);
            holders.put(holder.key, holder);
        }
    }

    @Nullable
    public static String getAsString(String key) {
        Object obj = holderValues.get(key);
        if (obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    @Nullable
    @SuppressWarnings({"unchecked"})
    public static List<String> getAsList(String key) {
        Object obj = holderValues.get(key);
        if (obj instanceof List<?>) {
            return (List<String>) obj;
        }
        return null;
    }

    private static void reloadEnum() {
        if (file == null || holders.isEmpty()) return;
        holderValues.clear();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.setDefaults(new YamlConfiguration());
        for (AbstractLanguageHolder holder : holders.values()) {
            if (!config.contains(holder.key)) {
                config.set(holder.key, holder.defaultValue);
                continue;
            }
            if (holder.isList) {
                holderValues.put(holder.key, config.getStringList(holder.key));
            } else {
                holderValues.put(holder.key, config.getString(holder.key));
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "更新语言文件时出现异常", e);
        }
    }

    protected static void init(PluginMain plugin) {
        Translation.plugin = plugin;
        file = new File(plugin.getDataFolder(), "gui.yml");
        IProvider.createAllProviders(placeholderProviders);
    }

    protected static void reloadConfig(ConfigurationSection config) {
        Translation.commonExtraLoreStat.clear();
        Translation.commonExtraLoreEmpty.clear();
        ConfigurationSection section = config.getConfigurationSection("common-stats");
        if (section != null) for (String key : section.getKeys(false)) {
            Translation.commonExtraLoreStat.put(key, section.getStringList(key + ".lore-stat"));
            Translation.commonExtraLoreEmpty.put(key, section.getStringList(key + ".lore-empty"));
        }
        Translation.translatedStatMap.clear();
        section = config.getConfigurationSection("stats");
        if (section != null) for (String key : section.getKeys(false)) {
            String name = section.getString(key + ".name");
            List<String> lore = section.getStringList(key + ".lore");
            List<String> loreStat;
            List<String> loreEmpty;
            String type = section.getString(key + ".options.from-common");
            if (type != null && Translation.commonExtraLoreStat.containsKey(type)) {
                loreStat = Translation.commonExtraLoreStat.get(type);
                loreEmpty = Translation.commonExtraLoreEmpty.get(type);
            } else {
                loreStat = section.getStringList(key + ".options.lore-stat");
                loreEmpty = section.getStringList(key + ".options.lore-empty");
            }
            Translation.translatedStatMap.put(key, new TranslatedStat(key, name, lore, loreStat, loreEmpty));
        }
        Translation.typeTranslation.clear();
        section = config.getConfigurationSection("types");
        if (section != null) for (String key : section.getKeys(false)) {
            Translation.typeTranslation.put(key, section.getString(key));
        }
        reloadEnum();
    }

    public static List<String> getCommonExtraLore(String type, boolean empty) {
        return (empty ? commonExtraLoreEmpty : commonExtraLoreStat).get(type);
    }

    public static String translateType(Type type) {
        return typeTranslation.get(type.getId());
    }

    public static String translateBoolean(boolean bool) {
        // TODO: 添加到语言文件
        return bool ? (ChatColor.GREEN + "是") : (ChatColor.RED + "否");
    }

    public static String translateBooleanOption(boolean bool) {
        // TODO: 添加到语言文件
        return bool ? (ChatColor.GREEN + "开") : (ChatColor.RED + "关");
    }

    public static String translateEnchant(Enchantment enchant) {
        // TODO: 翻译附魔名称
        return UtilityMethods.caseOnWords(enchant.getKey().getKey().replace("_", " "));
    }

    public static String translatePotion(PotionEffectType effectType) {
        // TODO: 翻译药水类型名称
        return UtilityMethods.caseOnWords(effectType.getName().toLowerCase().replace("_", " "));
    }

    public static String translateEnum(@Nullable Enum<?> enumValue) {
        if (enumValue == null) {
            return ChatColor.RED + "无"; // TODO: 添加到语言文件
        }
        // TODO: 从配置文件读取枚举翻译
        // 大部分都来自原版 Minecraft
        if (enumValue instanceof Material) {

        }
        if (enumValue instanceof PatternType) {

        }
        if (enumValue instanceof DyeColor) {

        }
        return ChatColor.GREEN + UtilityMethods.caseOnWords(enumValue.name().toLowerCase().replace("_", " "));
    }

    /**
     * 替换 Lore
     * @param stat 物品Stat
     * @param lore 待替换的Lore(只读)
     * @param raw 物品随机数据 (RandomStatData)
     * @return 替换后的Lore
     */
    @SuppressWarnings({"rawtypes"})
    public static List<String> replaceLore(@NotNull ItemStat stat, @NotNull List<String> lore, @Nullable Object raw) {
        List<String> list = new ArrayList<>();
        for (String s : lore) {
            for (IProvider provider : placeholderProviders) {
                String str = provider.replaceLine(stat, raw, s);
                if (str == null) continue;
                if (str.equals(IProvider.DELETE_FLAG)) {
                    s = null;
                    break;
                }
                s = str;
            }
            if (s == null) continue;
            if (s.contains("\n")) Collections.addAll(list, s.split("\n"));
            else list.add(s);
        }
        return list;
    }

    @NotNull
    @SuppressWarnings({"rawtypes"})
    public static TranslatedStat getStat(ItemStat stat) {
        TranslatedStat translated = translatedStatMap.get(stat.getId());
        if (translated == null) {
            // 缺省选项
            TranslatedStat ts = new TranslatedStat(stat);
            translatedStatMap.put(ts.id(), ts);
            return ts;
        }
        return translated;
    }
}
