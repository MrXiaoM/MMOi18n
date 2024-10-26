package top.mrxiaom.mmoi18n.placeholder;

import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.mmoi18n.placeholder.providers.*;
import top.mrxiaom.mmoi18n.placeholder.providers.stat.ChooseStatProvider;

import java.util.Collections;
import java.util.List;

/**
 * 变量提供器
 */
@SuppressWarnings({"rawtypes"})
public interface IProvider {
    /**
     * 返回 DELETE_FLAG 代表该变量所在的一整行都删除
     */
    String DELETE_FLAG = "\33:delete";

    /**
     * 替换一行 Lore
     * @param stat 物品Stat
     * @param raw 物品随机数据 (RandomStatData)
     * @param line 要替换的行
     * @return 替换后的 Lore，可返回多行，使用<code>\n</code>分隔，<br>
     * 返回 <code>null</code> 代表未替换，<br>
     * 返回 {@link IProvider#DELETE_FLAG} 代表删除这一行
     */
    @Nullable
    String replaceLine(ItemStat stat, Object raw, String line);

    static void createAllProviders(List<IProvider> collection) {
        Collections.addAll(collection,
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
        Collections.reverse(collection);
    }
}
