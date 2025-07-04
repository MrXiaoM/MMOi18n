package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.ParticleData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.jetbrains.annotations.Nullable;

import static top.mrxiaom.mmoi18n.Translation.translateEnum;

public class ParticleDataProvider extends AbstractProvider<ParticleData> {
    public ParticleDataProvider() {
        add("type", it -> translateEnum(it.getType()));
        add("particle", it -> translateEnum(it.getParticle()));
        add("modifiers_size", it -> it.getModifiers().size());
        add("color", it -> it.getColor().getRed() + "," + it.getColor().getGreen() + "," + it.getColor().getBlue());
        addIf("has_color", this::isColorable);
    }

    public boolean isColorable(ParticleData data) {
        return MMOUtils.isColorable(data.getParticle());
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof ParticleData data ? replace(line, data) : null;
    }
}
