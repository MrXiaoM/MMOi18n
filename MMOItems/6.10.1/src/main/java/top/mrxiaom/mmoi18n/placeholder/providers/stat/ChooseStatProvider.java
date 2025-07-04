package top.mrxiaom.mmoi18n.placeholder.providers.stat;

import io.lumine.mythic.lib.api.util.AltChar;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.ChooseStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.StatChoice;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChooseStatProvider extends AbstractStatProvider<ChooseStat, StringData, StringData> {
    public ChooseStatProvider() {
        add("found", this::foundId);
        add("choices", this::choices);
    }

    public String foundId(ChooseStat stat, StringData data) {
        StatChoice found = found(stat, data);
        // TODO: 添加到配置文件
        return found != null ? (ChatColor.GREEN + found.getId()) : (ChatColor.RED + "无");
    }

    @Nullable
    public StatChoice found(ChooseStat stat, StringData data) {
        return data == null ? null : stat.getChoice(data.toString());
    }

    @SuppressWarnings({"unchecked"})
    public String choices(ChooseStat stat, StringData data) {
        try {
            @Nullable StatChoice found = found(stat, data);
            Field field = ChooseStat.class.getDeclaredField("choices");
            field.setAccessible(true);
            List<StatChoice> choices = (List<StatChoice>) field.get(stat);
            List<String> list = new ArrayList<>();
            for (StatChoice existing : choices) {
                // Is it the one?
                String pick = existing.equals(found) ? ChatColor.RED.toString() + ChatColor.BOLD : ChatColor.GOLD.toString();
                list.add(pick + "  " + AltChar.smallListDash + " " + ChatColor.GRAY + existing.getId());
            }
            return String.join("\n", list);
        } catch (ReflectiveOperationException ignored) {
        }
        return "";
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        if (stat instanceof ChooseStat choose) {
            StringData data = raw instanceof StringData ? (StringData) raw : null;
            return replace(line, choose, data);
        }
        return null;
    }
}
