package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.random.RandomElementListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RandomElementListDataProvider extends AbstractProvider<RandomElementListData> {
    public RandomElementListDataProvider() {
        add("stats", this::stats);
    }

    public String stats(RandomElementListData data) {
        List<String> lore = new ArrayList<>();
        data.getKeys().forEach(key -> lore.add(ChatColor.GRAY + "* " + key.getKey().getName() + " " + key.getValue().getName() + ": " + ChatColor.RED + data.getStat(key.getKey(), key.getValue())));
        return String.join("\n", lore);
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof RandomElementListData data ? replace(line, data) : null;
    }
}
