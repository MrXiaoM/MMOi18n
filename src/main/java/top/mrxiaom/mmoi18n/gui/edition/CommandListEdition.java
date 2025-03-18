package top.mrxiaom.mmoi18n.gui.edition;

import io.lumine.mythic.lib.api.util.AltChar;
import io.lumine.mythic.lib.gui.Navigator;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import top.mrxiaom.mmoi18n.edition.StatEdition;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.mmoi18n.Translation.translateBoolean;
import static top.mrxiaom.mmoi18n.gui.ItemTag.has;
import static top.mrxiaom.mmoi18n.gui.ItemTag.put;

public class CommandListEdition extends EditionInventory {
	private static final int[] slots = { 19, 20, 21, 22, 23, 24, 25, 28, 29, 33, 34, 37, 38, 42, 43 };
	private static final NamespacedKey CONFIG_KEY = new NamespacedKey(MMOItems.plugin, "ConfigKey");

	public CommandListEdition(Navigator navigator, MMOItemTemplate template) {
		super(navigator, template);
	}

	@Override
	public String getName() {
		return "命令列表";
	}

	@Override
	public void arrangeInventory() {
		int n = 0;

		ConfigurationSection section = getEditedSection().getConfigurationSection("commands");
		if (section != null) for (String key : section.getKeys(false)) {

			String format = getEditedSection().getString("commands." + key + ".format");
			double delay = getEditedSection().getDouble("commands." + key + ".delay");
			boolean console = getEditedSection().getBoolean("commands." + key + ".console"),
					op = getEditedSection().getBoolean("commands." + key + ".op");

			final ItemStack item = new ItemStack(Material.COMPARATOR);
			ItemMeta itemMeta = item.getItemMeta();
			if (itemMeta != null) {
				itemMeta.setDisplayName(format == null || format.isEmpty() ? ChatColor.RED + "无格式" : ChatColor.GREEN + format);
				List<String> itemLore = new ArrayList<>();
				itemLore.add("");
				itemLore.add(ChatColor.GRAY + "命令延时: " + ChatColor.RED + delay);
				itemLore.add(ChatColor.GRAY + "控制台权限: " + ChatColor.RED + translateBoolean(console));
				itemLore.add(ChatColor.GRAY + "OP权限: " + ChatColor.RED + translateBoolean(op));
				itemLore.add("");
				itemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 移除.");
				itemMeta.setLore(itemLore);
				itemMeta.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, key);
			}
			item.setItemMeta(itemMeta);

			inventory.setItem(slots[n++], item);
		}

		ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta glassMeta = glass.getItemMeta();
		if (glassMeta != null) glassMeta.setDisplayName(ChatColor.RED + "- 无命令 -");
		glass.setItemMeta(glassMeta);

		ItemStack add = new ItemStack(Material.WRITABLE_BOOK);
		ItemMeta addMeta = add.getItemMeta();
		if (addMeta != null) addMeta.setDisplayName(ChatColor.GREEN + "添加一个命令...");
		add.setItemMeta(addMeta);
		put(add, "add_command");

		inventory.setItem(40, add);
		while (n < slots.length)
			inventory.setItem(slots[n++], glass);
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();

		event.setCancelled(true);
		if (event.getInventory() != event.getClickedInventory() || !MMOUtils.isMetaItem(item, false))
			return;

		if (has(item, "add_command")) {
			new StatEdition(this, ItemStats.COMMANDS).enable("请在聊天栏发送要添加的命令.",
					"",
					"要添加命令延时，请使用 " + ChatColor.RED + "-d:<延时时间>",
					"要命令以控制台权限执行，请使用 " + ChatColor.RED + "-c",
					"要命令以管理员权限执行(不推荐)，请使用 " + ChatColor.RED + "-op",
					"",
					ChatColor.YELLOW + "示例: -d:10.3 -op bc Hello, this is a test command.");
			return;
		}

		ItemMeta meta = item.getItemMeta();
		final String tag = meta == null ? null : meta.getPersistentDataContainer().get(CONFIG_KEY, PersistentDataType.STRING);
        if (tag == null || tag.isEmpty()) return;

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			ConfigurationSection section = getEditedSection().getConfigurationSection("commands");
			if (section != null && section.contains(tag)) {
				getEditedSection().set("commands." + tag, null);
				registerTemplateEdition();
				getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "成功移除命令 " + ChatColor.GOLD + tag + ChatColor.DARK_GRAY
						+ " (内部ID)" + ChatColor.GRAY + ".");
			}
		}
	}
}