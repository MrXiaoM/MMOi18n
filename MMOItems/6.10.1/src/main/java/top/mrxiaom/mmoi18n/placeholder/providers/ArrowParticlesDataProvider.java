package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.ArrowParticlesData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.jetbrains.annotations.Nullable;

import static top.mrxiaom.mmoi18n.Translation.translateEnum;

public class ArrowParticlesDataProvider extends AbstractProvider<ArrowParticlesData> {
    public ArrowParticlesDataProvider() {
        add("particle", it -> translateEnum(it.getParticle()));
        add("amount", ArrowParticlesData::getAmount);
        add("offset", ArrowParticlesData::getOffset);
        add("speed", ArrowParticlesData::getSpeed);
        add("red", ArrowParticlesData::getRed);
        add("green", ArrowParticlesData::getGreen);
        add("blue", ArrowParticlesData::getBlue);
        addIf("has_color", this::isColorable);
    }

    public boolean isColorable(ArrowParticlesData data) {
        return MMOUtils.isColorable(data.getParticle());
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof ArrowParticlesData data ? replace(line, data) : null;
    }
}
