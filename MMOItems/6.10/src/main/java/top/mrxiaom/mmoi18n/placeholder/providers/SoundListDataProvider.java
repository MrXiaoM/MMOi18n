package top.mrxiaom.mmoi18n.placeholder.providers;

import io.lumine.mythic.lib.UtilityMethods;
import net.Indyuce.mmoitems.stat.data.SoundListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SoundListDataProvider extends AbstractProvider<SoundListData> {
    public SoundListDataProvider() {
        add("sound_list", this::list);
    }

    public String list(SoundListData data) {
        List<String> lore = new ArrayList<>();
        data.mapData() // 音效感觉没啥好汉化的，汉化了反而看不懂是哪个音效ID了
                .forEach((sound,
                          soundData) -> lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN
                        + UtilityMethods.caseOnWords(sound.getName().toLowerCase().replace("-", " ").replace("_", " ")) + ChatColor.GRAY + ": "
                        + ChatColor.RED + soundData.getVolume() + " " + soundData.getPitch()));
        return String.join("\n", lore);
    }
    @Override
    @SuppressWarnings({"rawtypes"})
    public @Nullable String replaceLine(ItemStat stat, Object raw, String line) {
        return raw instanceof SoundListData data ? replace(line, data) : null;
    }
}
