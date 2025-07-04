package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.ColorData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.Nullable;

public class ColorDataProvider extends AbstractProvider<ColorData> {
    public ColorDataProvider() {
        add("red", ColorData::getRed);
        add("green", ColorData::getGreen);
        add("blue", ColorData::getBlue);
        add("to_string", ColorData::toString);
    }
    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof ColorData data ? replace(line, data) : null;
    }
}
