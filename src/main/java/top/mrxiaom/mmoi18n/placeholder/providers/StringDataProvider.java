package top.mrxiaom.mmoi18n.placeholder.providers;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.Nullable;

public class StringDataProvider extends AbstractProvider<StringData> {
    public StringDataProvider() {
        add("string_emit", it -> emit(it, 40));
        add("string", StringData::getString);
    }

    public String emit(StringData data, int length) {
        String value = MythicLib.plugin.parseColors(data.toString());
        return value.length() > length ? value.substring(0, length) + "..." : value;
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof StringData data ? replace(line, data) : null;
    }
}
