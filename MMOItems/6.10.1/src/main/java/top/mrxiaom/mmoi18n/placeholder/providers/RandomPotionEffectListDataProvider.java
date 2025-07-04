package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.mmoi18n.Translation.translatePotion;

public class RandomPotionEffectListDataProvider extends AbstractProvider<RandomPotionEffectListData> {
    public RandomPotionEffectListDataProvider() {
        add("potion_list_without_duration", it -> list(it, false));
        add("potion_list", it -> list(it, true));
    }

    public String list(RandomPotionEffectListData data, boolean duration) {
        List<String> lore = new ArrayList<>();

        for (RandomPotionEffectData effect : data.getEffects()) {
            String s = ChatColor.GRAY + "* " + ChatColor.GREEN + translatePotion(effect.getType())
                    + " " + effect.getAmplifier().toString();
            lore.add(duration ? (s + " " + ChatColor.GRAY + "(" + ChatColor.GREEN + effect.getDuration().toString()
                    + ChatColor.GRAY + "s)") : s);
        }
        return String.join("\n", lore);
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof RandomPotionEffectListData data ? replace(line, data) : null;
    }
}
