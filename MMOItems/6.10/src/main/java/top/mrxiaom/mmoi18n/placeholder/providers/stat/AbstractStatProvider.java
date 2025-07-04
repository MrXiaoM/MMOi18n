package top.mrxiaom.mmoi18n.placeholder.providers.stat;

import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.Pair;
import top.mrxiaom.mmoi18n.placeholder.IProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 适用于某一种 ItemStat 类型的变量提供器集合
 */
public abstract class AbstractStatProvider<T extends ItemStat<R, S>, R extends RandomStatData<S>, S extends StatData> implements IProvider<ItemStat<?, ?>> {
    private final List<Pair<String, BiFunction<T, R, Object>>> providers = new ArrayList<>();

    /**
     * 添加普通变量，格式 <code>$key</code>
     *
     * @param key 变量名
     * @param provider 变量提供器
     */
    protected void add(String key, BiFunction<T, R, Object> provider) {
        providers.add(Pair.of("$" + key, provider));
    }
    /**
     * 添加判断变量，用法：<br>
     * <code>$key$ </code> 和 <code>$!key$ </code><br>
     * 注意第二个 <code>$</code> 后面有空格。<br>
     * 在Lore中使用判断变量，不满足条件时，一整行都会被删除
     *
     * @param key 变量名
     * @param provider 布尔值提供器
     */
    protected void addIf(String key, BiFunction<T, R, Boolean> provider) {
        providers.add(Pair.of("$" + key + "$ ", (it1, it2) -> provider.apply(it1, it2) ? "" : null));
        providers.add(Pair.of("$!" + key + "$ ", (it1, it2) -> provider.apply(it1, it2) ? null : ""));
    }
    protected List<Pair<String, BiFunction<T, R, Object>>> providers() {
        return providers;
    }
    protected String replace(String s, T stat, R data) {
        for (Pair<String, BiFunction<T, R, Object>> pair : providers) {
            if (s.contains(pair.getKey())) {
                Object obj = pair.getValue().apply(stat, data);
                if (obj == null) {
                    return DELETE_FLAG;
                }
                s = s.replace(pair.getKey(), String.valueOf(obj));
            }
        }
        return s;
    }
}
