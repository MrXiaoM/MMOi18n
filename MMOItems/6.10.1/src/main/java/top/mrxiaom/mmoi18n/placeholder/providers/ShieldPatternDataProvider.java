package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.ShieldPatternData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.mmoi18n.Translation.translateEnum;

public class ShieldPatternDataProvider extends AbstractProvider<ShieldPatternData> {
    public ShieldPatternDataProvider() {
        add("base_color", it -> translateEnum(it.getBaseColor()));
        add("pattern_list", this::list);
    }

    public String list(ShieldPatternData data) {
        List<String> lore = new ArrayList<>();
        data.getPatterns().forEach(pattern -> lore.add(ChatColor.GRAY + "* " + translateEnum(pattern.getPattern()) + ChatColor.GRAY
                + " - " + translateEnum(pattern.getColor())));
        return String.join("\n", lore);
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof ShieldPatternData data ? replace(line, data) : null;
    }
}
