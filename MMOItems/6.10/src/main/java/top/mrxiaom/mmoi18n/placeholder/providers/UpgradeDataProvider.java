package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.UpgradeData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.Nullable;

import static top.mrxiaom.mmoi18n.Translation.translateBoolean;

public class UpgradeDataProvider extends AbstractProvider<UpgradeData> {
    public UpgradeDataProvider() {
        add("reference", UpgradeData::getReference);
        add("template", UpgradeData::getTemplate);
        add("workbench", it -> translateBoolean(it.isWorkbench()));
        add("destroy", it -> translateBoolean(it.isDestroy()));
        add("max", UpgradeData::getMax);
        add("min", UpgradeData::getMin);
        add("can_level_up", it -> translateBoolean(it.canLevelUp()));
        add("level", UpgradeData::getLevel);
    }
    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof UpgradeData data ? replace(line, data) : null;
    }
}
