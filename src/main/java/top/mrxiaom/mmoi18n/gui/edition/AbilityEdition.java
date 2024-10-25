package top.mrxiaom.mmoi18n.gui.edition;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.AltChar;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.mmoi18n.gui.ItemTag.has;
import static top.mrxiaom.mmoi18n.gui.ItemTag.put;

public class AbilityEdition extends EditionInventory {
	private final String configKey;

    private static final DecimalFormat MODIFIER_FORMAT = new DecimalFormat("0.###");
	private static final int[] slots = { 23, 24, 25, 32, 33, 34, 41, 42, 43, 50, 51, 52 };
	private static final NamespacedKey ABILITY_MOD_KEY = new NamespacedKey(MMOItems.plugin, "AbilityModifier");

	public AbilityEdition(Player player, MMOItemTemplate template, String configKey) {
		super(player, template);

		this.configKey = configKey;
	}

	@Override
	public String getName() {
		return "编辑能力";
	}

	@Override
	public void arrangeInventory() {
		int n = 0;

		String configString = getEditedSection().getString("ability." + configKey + ".type");
		String format = configString == null ? "" : configString.toUpperCase().replace(" ", "_").replace("-", "_").replaceAll("[^A-Z_]", "");
        RegisteredSkill ability = MMOItems.plugin.getSkills().hasSkill(format) ? MMOItems.plugin.getSkills().getSkill(format) : null;

		ItemStack abilityItem = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta abilityItemMeta = abilityItem.getItemMeta();
		if (abilityItemMeta != null) {
			abilityItemMeta.setDisplayName(ChatColor.GREEN + "能力");
			List<String> abilityItemLore = new ArrayList<>();
			abilityItemLore.add(ChatColor.GRAY + "选择武器可施展的能力.");
			abilityItemLore.add("");
			abilityItemLore.add(
					ChatColor.GRAY + "当前值: " + (ability == null ? ChatColor.RED + "未选择能力." : ChatColor.GOLD + ability.getName()));
			abilityItemLore.add("");
			abilityItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 选择.");
			abilityItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
			abilityItemMeta.setLore(abilityItemLore);
		}
		abilityItem.setItemMeta(abilityItemMeta);
		put(abilityItem, "ability");

		if (ability != null) {
			String castModeConfigString = getEditedSection().getString("ability." + configKey + ".mode");
			String castModeFormat = castModeConfigString == null ? ""
					: castModeConfigString.toUpperCase().replace(" ", "_").replace("-", "_").replaceAll("[^A-Z0-9_]", "");
			TriggerType castMode;
			try {
				castMode = TriggerType.valueOf(castModeFormat);
			} catch (RuntimeException exception) {
				castMode = null;
			}

			ItemStack castModeItem = new ItemStack(Material.ARMOR_STAND);
			ItemMeta castModeItemMeta = castModeItem.getItemMeta();
			if (castModeItemMeta != null) {
				castModeItemMeta.setDisplayName(ChatColor.GREEN + "触发器");
				List<String> castModeItemLore = new ArrayList<>();
				castModeItemLore.add(ChatColor.GRAY + "选择玩家施展能力的方式");
				castModeItemLore.add("");
				castModeItemLore.add(ChatColor.GRAY + "当前值: "
						+ (castMode == null ? ChatColor.RED + "未选择触发器." : ChatColor.GOLD + castMode.getName()));
				castModeItemLore.add("");
				castModeItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 选择.");
				castModeItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
				castModeItemMeta.setLore(castModeItemLore);
			}
			castModeItem.setItemMeta(castModeItemMeta);
			put(castModeItem, "trigger");

			inventory.setItem(30, castModeItem);
		}

		if (ability != null) {
			ConfigurationSection section = getEditedSection().getConfigurationSection("ability." + configKey);
			if (section != null) for (String modifier : ability.getHandler().getParameters()) {
				final ItemStack modifierItem = new ItemStack(Material.GRAY_DYE);
				ItemMeta modifierItemMeta = modifierItem.getItemMeta();
				if (modifierItemMeta != null) {
					modifierItemMeta.setDisplayName(ChatColor.GREEN + UtilityMethods.caseOnWords(modifier.toLowerCase().replace("-", " ")));
					List<String> modifierItemLore = new ArrayList<>();
					modifierItemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "这是一个能力修改器. 更改这个数值");
					modifierItemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "可以轻微地自定义能力.");
					modifierItemLore.add("");

					try {
						Object formula = section.get(modifier);
						modifierItemLore.add(ChatColor.GRAY + "当前值: " + ChatColor.GOLD
								+ (formula != null ? new NumericStatFormula(formula).toString()
								: MODIFIER_FORMAT.format(ability.getDefaultModifier(modifier))));
					} catch (IllegalArgumentException exception) {
						modifierItemLore.add(ChatColor.GRAY + "无法读取数值，使用默认值");
					}

					modifierItemLore.add(ChatColor.GRAY + "默认值: " + ChatColor.GOLD + MODIFIER_FORMAT.format(ability.getDefaultModifier(modifier)));
					modifierItemLore.add("");
					modifierItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 修改数值.");
					modifierItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
					modifierItemMeta.setLore(modifierItemLore);
					modifierItemMeta.getPersistentDataContainer().set(ABILITY_MOD_KEY, PersistentDataType.STRING, modifier);
				}
				modifierItem.setItemMeta(modifierItemMeta);

				inventory.setItem(slots[n++], modifierItem);
			}
		}

		ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta glassMeta = glass.getItemMeta();
		if (glassMeta != null) glassMeta.setDisplayName(ChatColor.RED + "- 无修改器 -");
		glass.setItemMeta(glassMeta);

		while (n < slots.length)
			inventory.setItem(slots[n++], glass);

		addEditionItems();
		inventory.setItem(28, abilityItem);
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();

		event.setCancelled(true);
		if (event.getInventory() != event.getClickedInventory() || !MMOUtils.isMetaItem(item, false))
			return;

		if (has(item, "edition_back")) {
			new AbilityListEdition(player, template).open(this);
			return;
		}

		if (has(item, "ability")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, ItemStats.ABILITIES, configKey, "ability").enable("请在聊天栏输入能力类型.",
						"你可以输入以下命令获取能力列表: " + ChatColor.AQUA + "/mi list ability");

			if (event.getAction() == InventoryAction.PICKUP_HALF) {
				if (getEditedSection().contains("ability." + configKey + ".type")) {
					getEditedSection().set("ability." + configKey, null);

					ConfigurationSection sectionAbility = getEditedSection().getConfigurationSection("ability");
					if (sectionAbility != null && sectionAbility.getKeys(false).isEmpty()) {
						getEditedSection().set("ability", null);
					}

					registerTemplateEdition();
					player.sendMessage(MMOItems.plugin.getPrefix() + "成功重置能力.");
				}
			}
			return;
		}

		if (has(item, "trigger")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL) {
				new StatEdition(this, ItemStats.ABILITIES, configKey, "mode").enable("请在聊天栏输入触发器类型.");

				player.sendMessage("");
				player.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "可用触发器列表");
				for (TriggerType castMode : TriggerType.values())
					player.sendMessage("* " + ChatColor.GREEN + castMode.name());
			}

			if (event.getAction() == InventoryAction.PICKUP_HALF && getEditedSection().contains("ability." + configKey + ".mode")) {
				getEditedSection().set("ability." + configKey + ".mode", null);
				registerTemplateEdition();
				player.sendMessage(MMOItems.plugin.getPrefix() + "成功重置能力触发器.");
			}
			return;
		}

		ItemMeta meta = item.getItemMeta();
		final String tag = meta == null ? null : meta.getPersistentDataContainer().get(ABILITY_MOD_KEY, PersistentDataType.STRING);
		if (tag == null || tag.isEmpty()) return;

		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(this, ItemStats.ABILITIES, configKey, tag).enable("请在聊天栏输入数值.");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			if (getEditedSection().contains("ability." + configKey + "." + tag)) {
				getEditedSection().set("ability." + configKey + "." + tag, null);
				registerTemplateEdition();
				player.sendMessage(MMOItems.plugin.getPrefix() + "成功重置 " + ChatColor.GOLD + UtilityMethods.caseOnWords(tag.replace("-", " "))
						+ ChatColor.GRAY + ".");
			}
		}
	}
}
