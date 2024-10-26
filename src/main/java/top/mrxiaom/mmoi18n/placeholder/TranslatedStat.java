package top.mrxiaom.mmoi18n.placeholder;

import net.Indyuce.mmoitems.stat.category.StatCategory;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.type.*;
import top.mrxiaom.mmoi18n.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TranslatedStat {
    private final String id;
    private final String name;
    private final List<String> lore;
    private final List<String> extraLore;
    private final List<String> extraLoreEmpty;

    public static String getCommonKeyByStat(ItemStat stat) {
        if (stat instanceof DoubleStat) {
            return "DOUBLE_STAT";
        } else if (stat instanceof ChooseStat) {
            return "CHOOSE_STAT";
        } else if (stat instanceof StringStat) {
            return "STRING_STAT";
        } else if (stat instanceof BooleanStat) {
            return "BOOLEAN_STAT";
        } else if (stat instanceof StringListStat) {
            return "STRING_LIST_STAT";
        } else {
            return null;
        }
    }

    private static List<String> fallbackLore(ItemStat stat) {
        List<String> lore = new ArrayList<>();
        lore.add("§8[ missing translation: " + stat.getId() + " ]");
        lore.add("§8  stat: " + stat.getClass().getName());
        lore.add("");
        Collections.addAll(lore, stat.getLore());
        return lore;
    }

    public TranslatedStat(ItemStat stat) {
        this(stat.getId(), stat.getName(), fallbackLore(stat), null, null);
    }

    public TranslatedStat(String id, String name, List<String> lore, List<String> extraLore, List<String> extraLoreEmpty) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.extraLore = extraLore;
        this.extraLoreEmpty = extraLoreEmpty;
    }

    public String id() {
        return id;
    }
    public String name() {
        return name;
    }
    public List<String> lore() {
        return lore;
    }

    public boolean translated() {
        return extraLore != null;
    }

    public String getLoreTag(ItemStat stat) {
        StatCategory category = stat.getCategory();
        // TODO: Lore Tag 的翻译
        return category == null ? null : category.getLoreTag();
    }

    public void whenDisplayed(ItemStat itemStat, List<String> lore, Optional<RandomStatData> data) {
        boolean empty = data.isEmpty();
        if (!translated()) {
            String type = getCommonKeyByStat(itemStat);
            if (type != null) {
                List<String> list = Translation.getCommonExtraLore(type, empty);
                if (list != null) {
                    lore.addAll(Translation.replaceLore(itemStat, list, data.orElse(null)));
                    return;
                }
            }
            itemStat.whenDisplayed(lore, data);
            return;
        }
        List<String> list = new ArrayList<>(empty ? extraLoreEmpty : extraLore);
        lore.addAll(Translation.replaceLore(itemStat, list, data.orElse(null)));
    }
}
