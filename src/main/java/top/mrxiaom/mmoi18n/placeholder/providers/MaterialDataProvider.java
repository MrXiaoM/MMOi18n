package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.MaterialData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.Nullable;

import static top.mrxiaom.mmoi18n.Translation.translateEnum;

public class MaterialDataProvider extends AbstractProvider<MaterialData> {
    public MaterialDataProvider() {
        add("material", it -> translateEnum(it.getMaterial()));
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof MaterialData data ? replace(line, data) : null;
    }
}
