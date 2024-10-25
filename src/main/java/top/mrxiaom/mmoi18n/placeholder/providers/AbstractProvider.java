package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.util.Pair;
import top.mrxiaom.mmoi18n.placeholder.IProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 适用于某一种 RandomStatData 类型的变量提供器集合
 */
@SuppressWarnings({"rawtypes"})
public abstract class AbstractProvider<T extends RandomStatData> implements IProvider {
    private final List<Pair<String, Function<T, Object>>> providers = new ArrayList<>();

    /**
     * 添加普通变量，格式 <code>$key</code>
     *
     * @param key 变量名
     * @param provider 变量提供器，返回 null 代表删除变量所在行
     */
    protected void add(String key, Function<T, Object> provider) {
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
    protected void addIf(String key, Function<T, Boolean> provider) {
        providers.add(Pair.of("$" + key + "$ ", it -> provider.apply(it) ? "" : null));
        providers.add(Pair.of("$!" + key + "$ ", it -> provider.apply(it) ? null : ""));
    }
    protected List<Pair<String, Function<T, Object>>> providers() {
        return providers;
    }
    protected String replace(String s, T data) {
        for (Pair<String, Function<T, Object>> pair : providers) {
            if (s.contains(pair.getKey())) {
                Object obj = pair.getValue().apply(data);
                if (obj == null) {
                    return DELETE_FLAG;
                }
                s = s.replace(pair.getKey(), String.valueOf(obj));
            }
        }
        return s;
    }
}
