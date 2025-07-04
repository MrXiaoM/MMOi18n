package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.ProjectileParticlesData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

public class ProjectileParticlesDataProvider extends AbstractProvider<ProjectileParticlesData> {
    public ProjectileParticlesDataProvider() {
        add("particle", ProjectileParticlesData::getParticle);
        add("color", this::color);
        addIf("has_color", this::isColorable);
    }

    public boolean isColorable(ProjectileParticlesData data) {
        return MMOUtils.isColorable(data.getParticle());
    }

    public String color(ProjectileParticlesData data) {
        Particle particle = data.getParticle();
        if ((MMOUtils.isColorable(particle))) {
            return particle == Particle.NOTE ? String.valueOf(data.getRed()) : data.getRed() + " " + data.getGreen() + " " + data.getBlue();
        } else {
            return "";
        }
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof ProjectileParticlesData data ? replace(line, data) : null;
    }
}
