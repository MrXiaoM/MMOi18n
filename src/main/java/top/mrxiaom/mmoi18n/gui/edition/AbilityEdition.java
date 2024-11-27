package top.mrxiaom.mmoi18n.gui.edition;

import com.google.common.collect.Lists;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.Indyuce.mmoitems.util.MMOUtils;
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
import top.mrxiaom.mmoi18n.language.IHolderAccessor;
import top.mrxiaom.mmoi18n.language.Language;
import top.mrxiaom.mmoi18n.language.LanguageEnumAutoHolder;

import java.text.DecimalFormat;
import java.util.List;

import static top.mrxiaom.mmoi18n.gui.ItemTag.has;
import static top.mrxiaom.mmoi18n.gui.ItemTag.put;
import static top.mrxiaom.mmoi18n.language.LanguageEnumAutoHolder.wrap;

public class AbilityEdition extends EditionInventory {
	@Language(prefix = "gui.item-edition.ability")
    public enum Msg implements IHolderAccessor {
        TITLE("编辑能力"),

		ITEM__ABILITY__DISPLAY("&a能力"),
		ITEM__ABILITY__LORE("&7选择武器可施展的能力.",
				"",
				"&7当前值: &6%s",
				"",
				"&e► 左键 选择.",
				"&e► 右键 重置."),
		ITEM__ABILITY__NOT_SELECT("&c未选择能力."),
		ITEM__ABILITY__TIPS(
				"请在聊天栏输入能力类型.",
				"你可以输入以下命令获取能力列表: &b/mi list ability"),
		ITEM__ABILITY__RESET("%s成功重置能力."),

		ITEM__CAST_MODE__DISPLAY("&a触发器"),
		ITEM__CAST_MODE__LORE("&7选择玩家施展能力的方式",
				"",
				"&7当前值: &6%s",
				"",
				"&e► 左键 选择.",
				"&e► 右键 重置."),
		ITEM__CAST_MODE__NOT_SELECT("&c未选择触发器."),
		ITEM__CAST_MODE__TIPS(Lists.newArrayList("请在聊天栏输入触发器类型.")),
		ITEM__CAST_MODE__LIST_HEADER("", "&a&l可用触发器列表"),
		ITEM__CAST_MODE__LIST_ITEM("* &a%s"),
		ITEM__CAST_MODE__RESET("%s成功重置能力触发器."),

		ITEM__MODIFIER__DISPLAY("&a%s"),
		ITEM__MODIFIER__DISPLAY_NONE("&c- 无修改器 -"),
		ITEM__MODIFIER__LORE("&7&o这是一个能力修改器. 更改这个数值",
				"&7&o可以轻微地自定义能力.",
				"",
				"&7%s",
				"&7默认值: &6%s",
				"",
				"&e► 左键 修改数值.",
				"&e► 右键 重置."),
		ITEM__MODIFIER__CURRENT_VALUE("当前值: &6%s"),
		ITEM__MODIFIER__INVALID_VALUE("无法读取数值，使用默认值"),
		ITEM__MODIFIER__TIPS(Lists.newArrayList("请在聊天栏输入数值.")),
		ITEM__MODIFIER__RESET("%s成功重置 &6%s&7."),

        ;
        Msg(String defaultValue) {
            holder = wrap(this, defaultValue);
        }
        Msg(String... defaultValue) {
            holder = wrap(this, defaultValue);
        }
        Msg(List<String> defaultValue) {
            holder = wrap(this, defaultValue);
        }
        private final LanguageEnumAutoHolder<Msg> holder;
        public LanguageEnumAutoHolder<Msg> holder() {
            return holder;
        }
    }
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
		return Msg.TITLE.str();
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
			abilityItemMeta.setDisplayName(Msg.ITEM__ABILITY__DISPLAY.str());
			String abilityName;
			if (ability == null) {
				abilityName = Msg.ITEM__ABILITY__NOT_SELECT.str();
			} else {
				abilityName = ability.getName();
			}
			abilityItemMeta.setLore(Msg.ITEM__ABILITY__LORE.list(abilityName));
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
				castModeItemMeta.setDisplayName(Msg.ITEM__CAST_MODE__DISPLAY.str());
				String castModeName;
				if (castMode == null) {
					castModeName = Msg.ITEM__CAST_MODE__NOT_SELECT.str();
				} else {
					castModeName = ability.getName();
				}
				castModeItemMeta.setLore(Msg.ITEM__CAST_MODE__LORE.list(castModeName));
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
					modifierItemMeta.setDisplayName(Msg.ITEM__MODIFIER__DISPLAY.str(UtilityMethods.caseOnWords(modifier.toLowerCase().replace("-", " "))));
					String valueLine;
					try {
						Object formula = section.get(modifier);
						valueLine = Msg.ITEM__MODIFIER__CURRENT_VALUE.str(
								(formula != null ? new NumericStatFormula(formula).toString()
								: MODIFIER_FORMAT.format(ability.getDefaultModifier(modifier))));
					} catch (IllegalArgumentException exception) {
						valueLine = Msg.ITEM__MODIFIER__INVALID_VALUE.str();
					}
					String defaultValue = MODIFIER_FORMAT.format(ability.getDefaultModifier(modifier));
					modifierItemMeta.setLore(Msg.ITEM__MODIFIER__LORE.list(valueLine, defaultValue));
					modifierItemMeta.getPersistentDataContainer().set(ABILITY_MOD_KEY, PersistentDataType.STRING, modifier);
				}
				modifierItem.setItemMeta(modifierItemMeta);

				inventory.setItem(slots[n++], modifierItem);
			}
		}

		ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta glassMeta = glass.getItemMeta();
		if (glassMeta != null) glassMeta.setDisplayName(Msg.ITEM__MODIFIER__DISPLAY_NONE.str());
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
				new StatEdition(this, ItemStats.ABILITIES, configKey, "ability").enable(Msg.ITEM__ABILITY__TIPS.array());

			if (event.getAction() == InventoryAction.PICKUP_HALF) {
				if (getEditedSection().contains("ability." + configKey + ".type")) {
					getEditedSection().set("ability." + configKey, null);

					ConfigurationSection sectionAbility = getEditedSection().getConfigurationSection("ability");
					if (sectionAbility != null && sectionAbility.getKeys(false).isEmpty()) {
						getEditedSection().set("ability", null);
					}

					registerTemplateEdition();
					player.sendMessage(Msg.ITEM__ABILITY__RESET.str(MMOItems.plugin.getPrefix()));
				}
			}
			return;
		}

		if (has(item, "trigger")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL) {
				new StatEdition(this, ItemStats.ABILITIES, configKey, "mode").enable(Msg.ITEM__CAST_MODE__TIPS.array());

				for (String s : Msg.ITEM__CAST_MODE__LIST_HEADER.list())
					player.sendMessage(s);

				for (TriggerType castMode : TriggerType.values())
					player.sendMessage(Msg.ITEM__CAST_MODE__LIST_ITEM.str(castMode.name()));
			}

			if (event.getAction() == InventoryAction.PICKUP_HALF && getEditedSection().contains("ability." + configKey + ".mode")) {
				getEditedSection().set("ability." + configKey + ".mode", null);
				registerTemplateEdition();
				player.sendMessage(Msg.ITEM__CAST_MODE__RESET.str(MMOItems.plugin.getPrefix()));
			}
			return;
		}

		ItemMeta meta = item.getItemMeta();
		final String tag = meta == null ? null : meta.getPersistentDataContainer().get(ABILITY_MOD_KEY, PersistentDataType.STRING);
		if (tag == null || tag.isEmpty()) return;

		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(this, ItemStats.ABILITIES, configKey, tag).enable(Msg.ITEM__MODIFIER__TIPS.array());

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			if (getEditedSection().contains("ability." + configKey + "." + tag)) {
				getEditedSection().set("ability." + configKey + "." + tag, null);
				registerTemplateEdition();
				player.sendMessage(Msg.ITEM__MODIFIER__RESET.str(
						MMOItems.plugin.getPrefix(),
						UtilityMethods.caseOnWords(tag.replace("-", " "))
				));
			}
		}
	}
}
