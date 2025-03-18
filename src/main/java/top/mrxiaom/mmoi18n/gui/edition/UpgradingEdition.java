package top.mrxiaom.mmoi18n.gui.edition;

import io.lumine.mythic.lib.api.util.AltChar;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.gui.Navigator;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import top.mrxiaom.mmoi18n.edition.StatEdition;
import top.mrxiaom.mmoi18n.gui.ItemTag;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.mmoi18n.Translation.translateBooleanOption;

public class UpgradingEdition extends EditionInventory {
	private static final ItemStack notAvailable = ItemFactory.of(Material.RED_STAINED_GLASS_PANE).name("&c不可用").build();

	public UpgradingEdition(Navigator navigator, MMOItemTemplate template) {
		super(navigator, template);
	}

	@Override
	public String getName() {
		return "升级向导: " + template.getId();
	}

	@Override
	public void arrangeInventory() {
		boolean workbench = getEditedSection().getBoolean("upgrade.workbench");
		if (!template.getType().corresponds(Type.CONSUMABLE)) {

			ItemStack workbenchItem = new ItemStack(Material.CRAFTING_TABLE);
			ItemMeta workbenchItemMeta = workbenchItem.getItemMeta();
			if (workbenchItemMeta != null) {
				workbenchItemMeta.setDisplayName(ChatColor.GREEN + "仅工作台升级?");
				List<String> workbenchItemLore = new ArrayList<>();
				workbenchItemLore.add(ChatColor.GRAY + "开启后, 玩家必须使用工作台");
				workbenchItemLore.add(ChatColor.GRAY + "配方来升级他们的武器.");
				workbenchItemLore.add("");
				workbenchItemLore.add(ChatColor.GRAY + "当前值: " + ChatColor.GOLD + translateBooleanOption(workbench));
				workbenchItemLore.add("");
				workbenchItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 点击更改数值.");
				workbenchItemMeta.setLore(workbenchItemLore);
			}
			workbenchItem.setItemMeta(workbenchItemMeta);
			ItemTag.put(workbenchItem, "workbench_upgrade_only");
			inventory.setItem(20, workbenchItem);

			String upgradeTemplate = getEditedSection().getString("upgrade.template");
			ItemStack templateItem = new ItemStack(Material.OAK_SIGN);
			ItemMeta templateItemMeta = templateItem.getItemMeta();
			if (templateItemMeta != null) {
				templateItemMeta.setDisplayName(ChatColor.GREEN + "升级模板");
				List<String> templateItemLore = new ArrayList<>();
				templateItemLore.add(ChatColor.GRAY + "这个选项决定了物品在升级时");
				templateItemLore.add(ChatColor.GRAY + "提升什么状态，详细请翻阅wiki.");
				templateItemLore.add("");
				templateItemLore.add(ChatColor.GRAY + "当前值: " + (upgradeTemplate == null ? ChatColor.RED + "无模板" : ChatColor.GOLD + upgradeTemplate));
				templateItemLore.add("");
				templateItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 输入模板.");
				templateItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
				templateItemMeta.setLore(templateItemLore);
			}
			templateItem.setItemMeta(templateItemMeta);
			ItemTag.put(templateItem, "upgrade_template");
			inventory.setItem(22, templateItem);

			int max = getEditedSection().getInt("upgrade.max");
			ItemStack maxItem = new ItemStack(Material.BARRIER);
			ItemMeta maxItemMeta = maxItem.getItemMeta();
			if (maxItemMeta != null) {
				maxItemMeta.setDisplayName(ChatColor.GREEN + "最高等级");
				List<String> maxItemLore = new ArrayList<>();
				maxItemLore.add(ChatColor.GRAY + "物品可以接受的最高等级");
				maxItemLore.add(ChatColor.GRAY + "(配方或消耗品).");
				maxItemLore.add("");
				maxItemLore.add(ChatColor.GRAY + "当前值: " + (max == 0 ? ChatColor.RED + "无限制" : ChatColor.GOLD + "" + max));
				maxItemLore.add("");
				maxItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
				maxItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
				maxItemMeta.setLore(maxItemLore);
			}
			maxItem.setItemMeta(maxItemMeta);
			ItemTag.put(maxItem, "max_upgrade");
			inventory.setItem(40, maxItem);

			int min = getEditedSection().getInt("upgrade.min", 0);
			ItemStack minItem = new ItemStack(Material.BARRIER);
			ItemMeta minItemMeta = minItem.getItemMeta();
			if (minItemMeta != null) {
				minItemMeta.setDisplayName(ChatColor.GREEN + "最低等级");
				List<String> minItemLore = new ArrayList<>();
				minItemLore.add(ChatColor.GRAY + "物品可以降级到的最低等级");
				minItemLore.add(ChatColor.GRAY + "(通过死亡或损坏).");
				minItemLore.add("");
				minItemLore.add(ChatColor.GRAY + "当前值: " + (min == 0 ? ChatColor.RED + "0" : ChatColor.GOLD + String.valueOf(min)));
				minItemLore.add("");
				minItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 修改数值.");
				minItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
				minItemMeta.setLore(minItemLore);
			}
			minItem.setItemMeta(minItemMeta);
			ItemTag.put(minItem, "min_upgrade");
			inventory.setItem(41, minItem);
		} else {
			inventory.setItem(20, notAvailable);
			inventory.setItem(22, notAvailable);
		}

		if (!workbench || template.getType().corresponds(Type.CONSUMABLE)) {

			String reference = getEditedSection().getString("upgrade.reference");
			ItemStack referenceItem = new ItemStack(Material.PAPER);
			ItemMeta referenceItemMeta = referenceItem.getItemMeta();
			if (referenceItemMeta != null) {
				referenceItemMeta.setDisplayName(ChatColor.GREEN + "升级参考");
				List<String> referenceItemLore = new ArrayList<>();
				referenceItemLore.add(ChatColor.GRAY + "这个选项决定了什么消耗品可以升级你的物品.");
				referenceItemLore.add(ChatColor.AQUA + "消耗品的升级参考必须与目标物品相匹配" + ChatColor.GRAY + "，");
				referenceItemLore.add(ChatColor.GRAY + "否则无法进行升级.");
				referenceItemLore.add("");
				referenceItemLore
						.add(ChatColor.GRAY + "当前值: " + (reference == null ? ChatColor.RED + "无参考" : ChatColor.GOLD + reference));
				referenceItemLore.add("");
				referenceItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 输入参考.");
				referenceItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
				referenceItemMeta.setLore(referenceItemLore);
			}
			referenceItem.setItemMeta(referenceItemMeta);
			ItemTag.put(referenceItem, "upgrade_reference");
			inventory.setItem(38, referenceItem);
		} else
			inventory.setItem(38, notAvailable);

		double success = getEditedSection().getDouble("upgrade.success");
		ItemStack successItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta successItemMeta = successItem.getItemMeta();
		if (successItemMeta != null) {
			successItemMeta.setDisplayName(ChatColor.GREEN + "成功率");
			List<String> successItemLore = new ArrayList<>();
			successItemLore.add(ChatColor.GRAY + "使用消耗品或配方升级");
			successItemLore.add(ChatColor.GRAY + "物品时，升级的成功概率.");
			successItemLore.add("");
			successItemLore.add(ChatColor.GRAY + "当前值: " + ChatColor.GOLD + (success == 0 ? "100" : "" + success) + "%");
			successItemLore.add("");
			successItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
			successItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
			successItemMeta.setLore(successItemLore);
		}
		successItem.setItemMeta(successItemMeta);
		ItemTag.put(successItem, "success_chance");
		inventory.setItem(24, successItem);

		if (success > 0 && !template.getType().corresponds(Type.CONSUMABLE)) {
			ItemStack destroyOnFail = new ItemStack(Material.FISHING_ROD);
			ItemMeta destroyOnFailMeta = destroyOnFail.getItemMeta();
			if (destroyOnFailMeta != null) {
				if (destroyOnFailMeta instanceof Damageable dmg) dmg.setDamage(30);
				destroyOnFailMeta.setDisplayName(ChatColor.GREEN + "失败后损毁?");
				List<String> destroyOnFailLore = new ArrayList<>();
				destroyOnFailLore.add(ChatColor.GRAY + "开启后, 物品将会在");
				destroyOnFailLore.add(ChatColor.GRAY + "升级失败后损毁.");
				destroyOnFailLore.add("");
				destroyOnFailLore.add(ChatColor.GRAY + "当前值: " + ChatColor.GOLD + translateBooleanOption(getEditedSection().getBoolean("upgrade.destroy")));
				destroyOnFailLore.add("");
				destroyOnFailLore.add(ChatColor.YELLOW + AltChar.listDash + " 点击更改数值.");
				destroyOnFailMeta.setLore(destroyOnFailLore);
			}
			destroyOnFail.setItemMeta(destroyOnFailMeta);
			ItemTag.put(destroyOnFail, "destroy_on_fail");
			inventory.setItem(42, destroyOnFail);
		}
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();

		event.setCancelled(true);
		if (event.getInventory() != event.getClickedInventory() || !MMOUtils.isMetaItem(item, false))
			return;

		if (ItemTag.has(item, "success_chance")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, ItemStats.UPGRADE, "rate").enable("请在聊天栏发送成功率.");

			if (event.getAction() == InventoryAction.PICKUP_HALF && getEditedSection().contains("upgrade.success")) {
				getEditedSection().set("upgrade.success", null);
				registerTemplateEdition();
				getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "成功重置成功率配置.");
			}
		}

		if (ItemTag.has(item, "max_upgrade")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, ItemStats.UPGRADE, "max").enable("请在聊天栏发送最大值.");

			if (event.getAction() == InventoryAction.PICKUP_HALF && getEditedSection().contains("upgrade.max")) {
				getEditedSection().set("upgrade.max", null);
				registerTemplateEdition();
				getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "成功重置升级最大值配置.");
			}
		}

		if (ItemTag.has(item, "min_upgrade")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, ItemStats.UPGRADE, "min").enable("请在聊天栏发送最小值.");

			if (event.getAction() == InventoryAction.PICKUP_HALF && getEditedSection().contains("upgrade.min")) {
				getEditedSection().set("upgrade.min", null);
				registerTemplateEdition();
				getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "成功重置升级最小值配置.");
			}
		}

		if (ItemTag.has(item, "upgrade_template")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, ItemStats.UPGRADE, "template").enable("请在聊天栏发送升级模板ID.");

			if (event.getAction() == InventoryAction.PICKUP_HALF && getEditedSection().contains("upgrade.template")) {
				getEditedSection().set("upgrade.template", null);
				registerTemplateEdition();
				getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "成功重置升级模板配置.");
			}
		}

		if (ItemTag.has(item, "upgrade_reference")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, ItemStats.UPGRADE, "ref").enable("请在聊天框发送升级参考物品 (文字).");

			if (event.getAction() == InventoryAction.PICKUP_HALF && getEditedSection().contains("upgrade.reference")) {
				getEditedSection().set("upgrade.reference", null);
				registerTemplateEdition();
				getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "已成功重置升级参考.");
			}
		}

		if (ItemTag.has(item, "workbench_upgrade_only")) {
			boolean bool = !getEditedSection().getBoolean("upgrade.workbench");
			getEditedSection().set("upgrade.workbench", bool);
			registerTemplateEdition();
			getPlayer().sendMessage(MMOItems.plugin.getPrefix()
					+ (bool ? "你的物品现在需要通过配方来升级." : "你的物品现在需要使用消耗品来升级."));
		}

		if (ItemTag.has(item, "destroy_on_fail")) {
			boolean bool = !getEditedSection().getBoolean("upgrade.destroy");
			getEditedSection().set("upgrade.destroy", bool);
			registerTemplateEdition();
			getPlayer().sendMessage(MMOItems.plugin.getPrefix()
					+ (bool ? "你的物品现在会在升级失败后损毁." : "你的物品现在不会在升级失败后损毁."));
		}
	}
}