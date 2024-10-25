package top.mrxiaom.mmoi18n.placeholder.providers;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StringListDataProvider extends AbstractProvider<StringListData> {
    public StringListDataProvider() {
        add("list", this::list);
        add("lore_list", this::lore);
        add("size", it -> it.getList().size());
    }

    public String list(StringListData data) {
        List<String> lore = new ArrayList<>();
        data.getList().forEach(str -> lore.add(ChatColor.GRAY + "* " + str));
        return String.join("\n", lore);
    }

    public String lore(StringListData data) {
        List<String> lore = MythicLib.plugin.parseColors(data.getList()
                .stream()
                .map(s -> ChatColor.GRAY + s)
                .toList());
        return String.join("\n", lore);
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof StringListData data ? replace(line, data) : null;
    }
}
