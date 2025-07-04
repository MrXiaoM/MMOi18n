package top.mrxiaom.mmoi18n;

import com.google.common.collect.Lists;
import io.lumine.mythic.lib.player.particle.ParticleEffectType;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.configuration.ConfigurationSection;
import top.mrxiaom.mmoi18n.gui.ItemBrowser;
import top.mrxiaom.mmoi18n.placeholder.TranslatedStat;
import top.mrxiaom.mmoi18n.placeholder.providers.*;
import top.mrxiaom.mmoi18n.placeholder.providers.stat.ChooseStatProvider;

import java.util.*;

public class TranslationImpl extends Translation<ItemStat<?, ?>, TranslatedStat> {
    public TranslationImpl(CommonPluginMain<ItemStat<?, ?>, TranslatedStat> plugin) {
        super(plugin);
        translateOthers = value -> {
            if (value instanceof ParticleEffectType) {
                return ((ParticleEffectType) value).getName();
            }
            return null;
        };
    }

    @Override
    public TranslatedStat newTranslatedStat(String key, String name, List<String> lore, List<String> loreStat, List<String> loreEmpty) {
        return new TranslatedStat(key, name, lore, loreStat, loreEmpty);
    }

    @Override
    public TranslatedStat newTranslatedStat(ItemStat<?, ?> itemStat) {
        return new TranslatedStat(itemStat);
    }

    @Override
    public String getItemStatId(ItemStat<?, ?> itemStat) {
        return itemStat.getId();
    }

    @Override
    public String getTranslatedStatId(TranslatedStat translatedStat) {
        return translatedStat.id();
    }

    @Override
    protected void createAllProviders() {
        Collections.addAll(placeholderProviders,
                // RandomStatData
                new ArrowParticlesDataProvider(),
                new ColorDataProvider(),
                new CommandListDataProvider(),
                new GemSocketsDataProvider(),
                new MaterialDataProvider(),
                new NameDataProvider(),
                new NumericStatFormulaProvider(),
                new ParticleDataProvider(),
                new ProjectileParticlesDataProvider(),
                new RandomAbilityListDataProvider(),
                new RandomBooleanDataProvider(),
                new RandomElementListDataProvider(),
                new RandomEnchantListDataProvider(),
                new RandomPotionEffectListDataProvider(),
                new RandomRestoreDataProvider(),
                new ShieldPatternDataProvider(),
                new SkullTextureDataProvider(),
                new SoundListDataProvider(),
                new StringDataProvider(),
                new StringListDataProvider(),
                new UpgradeDataProvider(),
                // ItemStat
                new ChooseStatProvider()
        );
        Collections.reverse(placeholderProviders);
        register(ItemBrowser.Msg.class, ItemBrowser.Msg::holder);
    }

    @Override
    public boolean resolveNotTranslated(ConfigurationSection config, ItemStat<?, ?> itemStat) {
        TranslatedStat stat = getStat(itemStat);
        if (stat.translated()) return false;

        String root = "stats." + stat.id();
        config.set(root + ".name", stat.name());
        config.set(root + ".lore", stat.lore());
        String fromCommon = TranslatedStat.getCommonKeyByStat(itemStat);
        if (fromCommon != null) {
            config.set(root + ".options.from-common", fromCommon);
        } else {
            config.set(root + ".options.lore-stat", Lists.newArrayList("§e► Click to"));
            config.set(root + ".options.lore-empty", Lists.newArrayList("§e► Click to"));
        }
        return true;
    }
}
