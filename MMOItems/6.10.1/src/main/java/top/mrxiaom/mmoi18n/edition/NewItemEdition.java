package top.mrxiaom.mmoi18n.edition;


import io.lumine.mythic.lib.gui.PluginInventory;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.Edition;
import net.Indyuce.mmoitems.api.edition.input.ChatEdition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import top.mrxiaom.mmoi18n.gui.ItemBrowser;

public class NewItemEdition implements Edition {
    private final ItemBrowser inv;
    private boolean successful;

    public NewItemEdition(ItemBrowser inv) {
        this.inv = inv;
    }

    @Override
    public PluginInventory getInventory() {
        return inv;
    }

    @Override
    public void enable(String... message) {
        inv.getPlayer().closeInventory();

        inv.getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
        inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "请在聊天栏发送新物品的ID.");
        inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "输入 'cancel' 取消创建.");

        // Default chat edition feature
        new ChatEdition(this);
        inv.getPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "创建物品", "详见聊天栏", 10, 40, 10);
    }

    @Override
    public boolean processInput(String input) {
        return successful = Bukkit.dispatchCommand(inv.getPlayer(),
                "mmoitems create " + inv.getType().getId() + " " + input.toUpperCase().replace(" ", "_").replace("-", "_"));
    }

    @Override
    public boolean shouldGoBack() {
        return !successful;
    }
}

