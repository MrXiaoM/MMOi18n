package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.NameData;
import org.jetbrains.annotations.Nullable;

public class NameDataProvider extends AbstractProvider<NameData> {
    public NameDataProvider() {
        add("prefixes_size", it -> it.getPrefixes().size());
        add("suffixes_size", it -> it.getSuffixes().size());
        add("main_name", NameData::getMainName);
        add("bake", NameData::bake);
    }
    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof NameData data ? replace(line, data) : null;
    }
}
