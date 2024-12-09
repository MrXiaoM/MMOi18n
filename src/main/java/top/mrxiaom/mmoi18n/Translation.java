package top.mrxiaom.mmoi18n;

import io.lumine.mythic.lib.UtilityMethods;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.*;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.mmoi18n.gui.ItemBrowser;
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
    private static final boolean supportKeyed = isClassPresent("org.bukkit.Keyed");

    private static final String[] commonTranslation = new String[5];

    public static boolean isClassPresent(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
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
        YamlConfiguration config = file.exists()
                ? YamlConfiguration.loadConfiguration(file)
                : new YamlConfiguration();
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
            plugin.getLogger().log(Level.WARNING, "更新语言文件 gui.yml 时出现异常", e);
        }
    }

    protected static void init(PluginMain plugin) {
        Translation.plugin = plugin;
        file = new File(plugin.getDataFolder(), "gui.yml");
        IProvider.createAllProviders(placeholderProviders);
        register(ItemBrowser.Msg.class, ItemBrowser.Msg::holder);
    }

    protected static void reloadConfig(ConfigurationSection pluginConfig) {
        File file = new File(plugin.getDataFolder(), "stats.yml");
        if (!file.exists()) {
            plugin.saveResource("stats.yml", true);
        }
        if (!file.exists()) {
            plugin.getLogger().log(Level.WARNING, "保存默认语言文件 stats.yml 保存失败");
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.setDefaults(new YamlConfiguration());
        ConfigurationSection section;

        // -------------- stats.yml ------------------
        commonExtraLoreStat.clear();
        commonExtraLoreEmpty.clear();
        translatedStatMap.clear();
        section = config.getConfigurationSection("common-stats");
        if (section != null) for (String key : section.getKeys(false)) {
            commonExtraLoreStat.put(key, section.getStringList(key + ".lore-stat"));
            commonExtraLoreEmpty.put(key, section.getStringList(key + ".lore-empty"));
        }
        section = config.getConfigurationSection("stats");
        if (section != null) for (String key : section.getKeys(false)) {
            String name = section.getString(key + ".name");
            List<String> lore = section.getStringList(key + ".lore");
            List<String> loreStat;
            List<String> loreEmpty;
            String type = section.getString(key + ".options.from-common");
            if (type != null && commonExtraLoreStat.containsKey(type)) {
                loreStat = commonExtraLoreStat.get(type);
                loreEmpty = commonExtraLoreEmpty.get(type);
            } else {
                loreStat = section.getStringList(key + ".options.lore-stat");
                loreEmpty = section.getStringList(key + ".options.lore-empty");
            }
            translatedStatMap.put(key, new TranslatedStat(key, name, lore, loreStat, loreEmpty));
        }

        // -------------- config.yml ------------------
        typeTranslation.clear();
        section = pluginConfig.getConfigurationSection("types");
        if (section != null) for (String key : section.getKeys(false)) {
            typeTranslation.put(key, section.getString(key));
        }
        commonTranslation[0] = pluginConfig.getString("common.true", "§a是");
        commonTranslation[1] = pluginConfig.getString("common.false", "§c否");
        commonTranslation[2] = pluginConfig.getString("common.on", "§a开");
        commonTranslation[3] = pluginConfig.getString("common.off", "§c关");
        commonTranslation[4] = pluginConfig.getString("common.none", "§c无");
        // ---------------- gui.yml -------------------
        reloadEnum();
    }

    public static List<String> getCommonExtraLore(String type, boolean empty) {
        return (empty ? commonExtraLoreEmpty : commonExtraLoreStat).get(type);
    }

    public static String translateType(Type type) {
        return typeTranslation.get(type.getId());
    }

    public static String translateBoolean(boolean bool) {
        return commonTranslation[bool ? 0 : 1]; // 是|否
    }

    public static String translateBooleanOption(boolean bool) {
        return commonTranslation[bool ? 2 : 3]; // 开|关
    }

    public static String translateEnchant(Enchantment enchant) {
        // TODO: 翻译附魔名称
        return UtilityMethods.caseOnWords(enchant.getKey().getKey().replace("_", " "));
    }

    public static String translatePotion(PotionEffectType effectType) {
        // TODO: 翻译药水类型名称
        return UtilityMethods.caseOnWords(effectType.getName().toLowerCase().replace("_", " "));
    }

    public static String translateEnum(@Nullable Object value) {
        if (value == null) {
            return commonTranslation[4]; // 无
        }
        if (supportKeyed) {
            if (value instanceof Keyed keyed) {
                NamespacedKey key = keyed.getKey();
                // TODO: 通过 key 读取枚举翻译
                return ChatColor.GREEN + UtilityMethods.caseOnWords(key.getKey().toLowerCase().replace("_", " "));
            }
        }
        if (!(value instanceof Enum<?> enumValue)) return commonTranslation[4];

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

    public static Set<String> allStats() {
        return translatedStatMap.keySet();
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
