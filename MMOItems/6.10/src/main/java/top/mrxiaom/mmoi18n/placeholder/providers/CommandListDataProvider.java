package top.mrxiaom.mmoi18n.placeholder.providers;

import net.Indyuce.mmoitems.stat.data.CommandListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.Nullable;


public class CommandListDataProvider extends AbstractProvider<CommandListData> {
    public CommandListDataProvider() {
        add("size", it -> it.getCommands().size());
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof CommandListData data ? replace(line, data) : null;
    }
}
