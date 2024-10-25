package top.mrxiaom.mmoi18n.gui.edition;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.AltChar;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import net.Indyuce.mmoitems.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.mmoi18n.gui.ItemTag.has;
import static top.mrxiaom.mmoi18n.gui.ItemTag.put;

public class ArrowParticlesEdition extends EditionInventory {
	public ArrowParticlesEdition(Player player, MMOItemTemplate template) {
		super(player, template);
	}

	@Override
	public String getName() {
		return "箭轨迹粒子: " + template.getId();
	}

	@Override
	public void arrangeInventory() {
		Particle particle = null;
		try {
			particle = Particle.valueOf(getEditedSection().getString("arrow-particles.particle"));
		} catch (Exception ignored) {}

		ItemStack particleItem = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta particleItemMeta = particleItem.getItemMeta();
		particleItemMeta.setDisplayName(ChatColor.GREEN + "粒子类型");
		List<String> particleItemLore = new ArrayList<>();
		particleItemLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "The particle which is displayed around the");
		particleItemLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "arrow. Fades away when the arrow lands.");
		particleItemLore.add("");
		particleItemLore.add(ChatColor.GRAY + "当前值: " + (particle == null ? ChatColor.RED + "未选择粒子."
				: ChatColor.GOLD + UtilityMethods.caseOnWords(particle.name().toLowerCase().replace("_", " "))));
		particleItemLore.add("");
		particleItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
		particleItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
		particleItemMeta.setLore(particleItemLore);
		particleItem.setItemMeta(particleItemMeta);
		put(particleItem, "particle");

		ItemStack amount = new ItemStack(Material.GRAY_DYE);
		ItemMeta amountMeta = amount.getItemMeta();
		amountMeta.setDisplayName(ChatColor.GREEN + "数量");
		List<String> amountLore = new ArrayList<>();
		amountLore.add("");
		amountLore.add(ChatColor.GRAY + "当前值: " + ChatColor.GOLD + getEditedSection().getInt("arrow-particles.amount"));
		amountLore.add("");
		amountLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
		amountLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
		amountMeta.setLore(amountLore);
		amount.setItemMeta(amountMeta);
		put(amount, "amount");

		ItemStack offset = new ItemStack(Material.GRAY_DYE);
		ItemMeta offsetMeta = offset.getItemMeta();
		offsetMeta.setDisplayName(ChatColor.GREEN + "偏移值");
		List<String> offsetLore = new ArrayList<>();
		offsetLore.add("");
		offsetLore.add(ChatColor.GRAY + "当前值: " + ChatColor.GOLD + getEditedSection().getDouble("arrow-particles.offset"));
		offsetLore.add("");
		offsetLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
		offsetLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
		offsetMeta.setLore(offsetLore);
		offset.setItemMeta(offsetMeta);
		put(offset, "offset");

		if (particle != null) {
			ConfigurationSection section = getEditedSection().getConfigurationSection("arrow-particles");
			if (MMOUtils.isColorable(particle)) {
				int red = section.getInt("color.red");
				int green = section.getInt("color.green");
				int blue = section.getInt("color.blue");

				ItemStack colorItem = new ItemStack(Material.GRAY_DYE);
				ItemMeta colorMeta = colorItem.getItemMeta();
				colorMeta.setDisplayName(ChatColor.GREEN + "粒子颜色");
				List<String> colorLore = new ArrayList<>();
				colorLore.add("");
				colorLore.add(ChatColor.GRAY + "当前值 (R-G-B):");
				colorLore.add("" + ChatColor.RED + ChatColor.BOLD + red + ChatColor.GRAY + " - " + ChatColor.GREEN + ChatColor.BOLD + green
						+ ChatColor.GRAY + " - " + ChatColor.BLUE + ChatColor.BOLD + blue);
				colorLore.add("");
				colorLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
				colorLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
				colorMeta.setLore(colorLore);
				colorItem.setItemMeta(colorMeta);
				put(colorItem, "particle_color");

				inventory.setItem(41, colorItem);
			} else {
				ItemStack speedItem = new ItemStack(Material.GRAY_DYE);
				ItemMeta speedItemMeta = speedItem.getItemMeta();
				speedItemMeta.setDisplayName(ChatColor.GREEN + "速度");
				List<String> speedItemLore = new ArrayList<>();
				speedItemLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "The speed at which your particle");
				speedItemLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "flies off in random directions.");
				speedItemLore.add("");
				speedItemLore.add(ChatColor.GRAY + "当前值: " + ChatColor.GOLD + section.getDouble("speed"));
				speedItemLore.add("");
				speedItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
				speedItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
				speedItemMeta.setLore(speedItemLore);
				speedItem.setItemMeta(speedItemMeta);
				put(speedItem, "speed");

				inventory.setItem(41, speedItem);
			}
		}

		inventory.setItem(30, particleItem);
		inventory.setItem(23, amount);
		inventory.setItem(32, offset);
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();

		event.setCancelled(true);
		if (event.getInventory() != event.getClickedInventory() || !MMOUtils.isMetaItem(item, false))
			return;

		if (has(item, "particle")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, ItemStats.ARROW_PARTICLES, "particle").enable("请在聊天栏发送粒子类型.");

			if (event.getAction() == InventoryAction.PICKUP_HALF) {
				if (getEditedSection().contains("arrow-particles.particle")) {
					getEditedSection().set("arrow-particles", null);
					registerTemplateEdition();
					player.sendMessage(MMOItems.plugin.getPrefix() + "成功重置粒子类型.");
				}
			}
		}

		if (has(item, "particle_color")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, ItemStats.ARROW_PARTICLES, "color").enable("请在聊天栏发送RGB颜色.",
						ChatColor.AQUA + "格式: [红] [绿] [蓝]");

			if (event.getAction() == InventoryAction.PICKUP_HALF) {
				if (getEditedSection().contains("arrow-particles.color")) {
					getEditedSection().set("arrow-particles.color", null);
					registerTemplateEdition();
					player.sendMessage(MMOItems.plugin.getPrefix() + "成功重置粒子颜色.");
				}
			}
		}

		for (Pair pair : new Pair[] {
				Pair.of("amount", "数量"), Pair.of("offset", "偏移值"), Pair.of("speed", "速度")
		})
			if (has(item, (String) pair.getKey())) {
				String type = (String) pair.getKey();
				String translated = (String) pair.getValue();
				if (event.getAction() == InventoryAction.PICKUP_ALL)
					new StatEdition(this, ItemStats.ARROW_PARTICLES, type).enable("请在聊天栏发送" + translated);

				if (event.getAction() == InventoryAction.PICKUP_HALF) {
					if (getEditedSection().contains("arrow-particles." + type)) {
						getEditedSection().set("arrow-particles." + type, null);
						registerTemplateEdition();
						player.sendMessage(MMOItems.plugin.getPrefix() + "成功重置" + translated + ".");
					}
				}
			}
	}
}