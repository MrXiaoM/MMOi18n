package top.mrxiaom.mmoi18n.language;

import com.google.common.collect.Lists;
import net.Indyuce.mmoitems.util.Pair;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.mmoi18n.Translation;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLanguageHolder {
    public final String key;
    public final boolean isList;
    public final Object defaultValue;

    public AbstractLanguageHolder(@NotNull String key, boolean isList, Object defaultValue) {
        this.key = key;
        this.isList = isList;
        this.defaultValue = defaultValue;
    }

    @SuppressWarnings({"unchecked"})
    private <T> T getOrDefault(T value) {
        return value == null ? (T) defaultValue : value;
    }

    public String str() {
        if (isList) {
            List<String> list = getOrDefault(Translation.getAsList(key));
            return String.join("\n", list);
        } else {
            return getOrDefault(Translation.getAsString(key));
        }
    }
    public String str(Object... args) {
        return String.format(str(), args);
    }
    @SafeVarargs
    public final String str(Pair<String, Object>... replacements) {
        return replace(str(), replacements);
    }
    public List<String> list() {
        if (isList) {
            return getOrDefault(Translation.getAsList(key));
        } else {
            String str = getOrDefault(Translation.getAsString(key));
            return Lists.newArrayList(str.split("\n"));
        }
    }
    public List<String> list(Object... args) {
        return list().stream()
                .map(it -> String.format(it, args))
                .collect(Collectors.toList());
    }
    @SafeVarargs
    public final List<String> list(Pair<String, Object>... replacements) {
        return list().stream()
                .map(it -> replace(str(), replacements))
                .collect(Collectors.toList());
    }

    public static String replace(String s, Pair<String, Object>[] replacements) {
        for (Pair<String, Object> replacement : replacements) {
            s = s.replace(replacement.getKey(), String.valueOf(replacement.getValue()));
        }
        return s;
    }
}
