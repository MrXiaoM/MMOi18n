package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.random.RandomRestoreData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class RandomRestoreDataProvider extends AbstractProvider<RandomRestoreData> {
    NumericStatFormulaProvider provider;
    public RandomRestoreDataProvider() {
        provider = new NumericStatFormulaProvider();
        numeric("health", RandomRestoreData::getHealth);
        numeric("food", RandomRestoreData::getHealth);
        numeric("saturation", RandomRestoreData::getHealth);
    }

    private void numeric(String prefix, Function<RandomRestoreData, NumericStatFormula> tranform) {
        for (Pair<String, Function<NumericStatFormula, Object>> pair : provider.providers()) {
            String key = prefix + "_" + pair.getKey();
            add(key, it -> pair.getValue().apply(tranform.apply(it)));
        }
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof RandomRestoreData data ? replace(line, data) : null;
    }
}
