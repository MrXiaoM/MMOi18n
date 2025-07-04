package top.mrxiaom.mmoi18n;

import io.lumine.mythic.lib.UtilityMethods;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.event.Listener;
import top.mrxiaom.mmoi18n.gui.listener.GuiListener;
import top.mrxiaom.mmoi18n.listener.ItemUpdater;
import top.mrxiaom.mmoi18n.listener.ItemUpdaterImpl;
import top.mrxiaom.mmoi18n.placeholder.TranslatedStat;

import java.util.Collection;

public class PluginMain extends CommonPluginMain<ItemStat<?, ?>, TranslatedStat> {
    @Override
    protected Translation<ItemStat<?, ?>, TranslatedStat> createTranslation() {
        return new TranslationImpl(this);
    }

    @Override
    protected Listener createGuiListener() {
        return new GuiListener(this);
    }

    @Override
    protected ItemUpdater<ItemStat<?, ?>, TranslatedStat> createItemUpdater() {
        return new ItemUpdaterImpl(this);
    }

    @Override
    protected Collection<ItemStat<?, ?>> getAllStats() {
        return MMOItems.plugin.getStats().getAll();
    }

    @Override
    protected boolean isNotInternalStat(ItemStat<?, ?> itemStat) {
        return !(itemStat instanceof InternalStat);
    }

    @Override
    protected void closeOpenViewsOfType(Class<?> type) {
        UtilityMethods.closeOpenViewsOfType(type);
    }
}
