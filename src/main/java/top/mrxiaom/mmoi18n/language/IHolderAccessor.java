package top.mrxiaom.mmoi18n.language;

import net.Indyuce.mmoitems.util.Pair;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface IHolderAccessor {

    AbstractLanguageHolder holder();
    
    default String str() {
        return holder().str();
    }
    default String str(Object... args) {
        return holder().str(args);
    }
    default String str(Pair... replacements) {
        return holder().str(replacements);
    }
    default List<String> list() {
        return holder().list();
    }
    default List<String> list(Object... args) {
        return holder().list(args);
    }
    default List<String> list(Pair... replacements) {
        Pair<String, Object>[] array = new Pair[replacements.length];
        System.arraycopy(replacements, 0, array, 0, replacements.length);
        return holder().list(array);
    }
    default String[] array() {
        return holder().array();
    }
    default String[] array(Object... args) {
        return holder().array(args);
    }
    default String[] array(Pair... replacements) {
        Pair<String, Object>[] array = new Pair[replacements.length];
        System.arraycopy(replacements, 0, array, 0, replacements.length);
        return holder().array(array);
    }
}
