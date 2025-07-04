package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.SkullTextureData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.Nullable;

import static top.mrxiaom.mmoi18n.Translation.translateBoolean;

public class SkullTextureDataProvider extends AbstractProvider<SkullTextureData> {
    public SkullTextureDataProvider() {
        add("has", it -> translateBoolean(it.getGameProfile() != null));
    }
    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof SkullTextureData data ? replace(line, data) : null;
    }
}
