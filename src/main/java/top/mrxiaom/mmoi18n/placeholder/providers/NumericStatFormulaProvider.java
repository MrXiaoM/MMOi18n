package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

import static top.mrxiaom.mmoi18n.Translation.translateBoolean;

public class NumericStatFormulaProvider extends AbstractProvider<NumericStatFormula> {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.####");
    public NumericStatFormulaProvider() {
        add("base_int", it -> (int) it.getBase());
        add("base", it -> DECIMAL_FORMAT.format(it.getBase()));
        add("scale_formatted", it -> (it.getScale() != 0 ? ChatColor.GRAY + " (+" + ChatColor.GREEN + DECIMAL_FORMAT.format(it.getScale()) + ChatColor.GRAY + "/Lvl)" : ""));
        add("scale", it -> DECIMAL_FORMAT.format(it.getScale()));
        add("spread_percent", it -> DECIMAL_FORMAT.format(it.getSpread() * 100));
        add("spread", it -> DECIMAL_FORMAT.format(it.getSpread()));
        add("max_spread_percent", it -> DECIMAL_FORMAT.format(it.getMaxSpread() * 100));
        add("max_spread", it -> DECIMAL_FORMAT.format(it.getMaxSpread()));
        add("uniform", it -> translateBoolean(it.isUniform()));
        add("min", it -> DECIMAL_FORMAT.format(it.getMin()));
        add("max", it -> DECIMAL_FORMAT.format(it.getMax()));
        add("formatted", this::format);
        addIf("is_uniform", NumericStatFormula::isUniform);
        addIf("has_spread", it -> it.getSpread() > 0);
        addIf("has_min", NumericStatFormula::hasMin);
        addIf("has_max", NumericStatFormula::hasMax);
    }

    public String format(NumericStatFormula data) {
        // TODO: 从配置文件读取格式
        return data.toString();
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof NumericStatFormula data ? replace(line, data) : null;
    }
}
