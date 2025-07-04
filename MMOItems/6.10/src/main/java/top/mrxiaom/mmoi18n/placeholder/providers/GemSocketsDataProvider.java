package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GemSocketsDataProvider extends AbstractProvider<GemSocketsData> {
    public GemSocketsDataProvider() {
        add("empty_size", it -> it.getEmptySlots().size());
        add("gems_size", it -> it.getGems().size());
        add("list", this::list);
    }

    public String list(GemSocketsData data) {
        List<String> lore = new ArrayList<>();
        // TODO: 添加到配置文件
        data.getEmptySlots().forEach(socket -> lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + socket + " Gem Socket"));
        return String.join("\n", lore);
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof GemSocketsData data ? replace(line, data) : null;
    }
}
