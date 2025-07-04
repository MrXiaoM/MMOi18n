package top.mrxiaom.mmoi18n.placeholder.providers;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmoitems.stat.data.random.RandomBooleanData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import static top.mrxiaom.mmoi18n.Translation.translateBooleanOption;

public class RandomBooleanDataProvider extends AbstractProvider<RandomBooleanData> {
    public RandomBooleanDataProvider() {
        add("formatted", this::format);
        add("chance_formatted", this::formatChance);
        add("chance", RandomBooleanData::getChance);
    }
    public String format(RandomBooleanData data) {
        double chance = data.getChance();
        return (chance >= 1 ? translateBooleanOption(true)
                : chance <= 0 ? translateBooleanOption(false)
                // TODO: 加入到语言文件
                : ChatColor.GREEN + formatChance(data) + "% 几率");
    }

    public String formatChance(RandomBooleanData data) {
        return MythicLib.plugin.getMMOConfig().decimal.format(data.getChance() * 100);
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof RandomBooleanData data ? replace(line, data) : null;
    }
}
