package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.random.RandomEnchantListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.mmoi18n.Translation.translateEnchant;

public class RandomEnchantListDataProvider extends AbstractProvider<RandomEnchantListData> {
    public RandomEnchantListDataProvider() {
        add("enchants", this::enchants);
    }

    public String enchants(RandomEnchantListData data) {
        List<String> lore = new ArrayList<>();
        data.getEnchants().forEach(enchant -> lore.add(ChatColor.GRAY + "* " + translateEnchant(enchant)
                + " " + data.getLevel(enchant).toString()));
        return String.join("\n", lore);
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof RandomEnchantListData data ? replace(line, data) : null;
    }
}
