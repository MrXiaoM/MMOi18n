package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.random.RandomAbilityListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.Nullable;

public class RandomAbilityListDataProvider extends AbstractProvider<RandomAbilityListData> {
    public RandomAbilityListDataProvider() {
        add("abilities_size", it -> it.getAbilities().size());
    }
    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof RandomAbilityListData data ? replace(line, data) : null;
    }
}
