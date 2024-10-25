package top.mrxiaom.mmoi18n.gui.listener;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.mmoi18n.PluginMain;
import top.mrxiaom.mmoi18n.gui.InjectedInventory;
import top.mrxiaom.mmoi18n.gui.ItemBrowser;
import top.mrxiaom.mmoi18n.gui.ItemTag;
import top.mrxiaom.mmoi18n.gui.edition.*;
import top.mrxiaom.mmoi18n.gui.edition.recipe.RecipeListGUI;
import top.mrxiaom.mmoi18n.gui.edition.recipe.RecipeTypeListGUI;
import top.mrxiaom.mmoi18n.gui.edition.recipe.gui.RecipeEditorGUI;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

public class GuiListener implements Listener {
	PluginMain plugin;
	public GuiListener(PluginMain plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void a(InventoryOpenEvent event) {
		HumanEntity player = event.getPlayer();
		Inventory inventory = event.getInventory();
		InventoryHolder holder = inventory.getHolder();
		if (holder instanceof net.Indyuce.mmoitems.gui.ItemBrowser old
		&& !(holder instanceof InjectedInventory)) {
			event.setCancelled(true);
			new ItemBrowser(old.getPlayer(), old.getType()).open();
			return;
		}
		if (holder instanceof net.Indyuce.mmoitems.gui.edition.EditionInventory
		&& !(holder instanceof InjectedInventory)) {
			event.setCancelled(true);
			try {
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.AbilityEdition old) {
					Field field = old.getClass().getDeclaredField("configKey");
					field.setAccessible(true);
					new AbilityEdition(old.getPlayer(), old.getEdited(), (String) field.get(old)).open();
					return;
				}
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.AbilityListEdition old) {
					new AbilityListEdition(old.getPlayer(), old.getEdited()).open();
					return;
				}
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.ArrowParticlesEdition old) {
					new ArrowParticlesEdition(old.getPlayer(), old.getEdited()).open();
					return;
				}
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.CommandListEdition old) {
					new CommandListEdition(old.getPlayer(), old.getEdited()).open();
					return;
				}
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.ElementsEdition old) {
					new ElementsEdition(old.getPlayer(), old.getEdited()).open();
					return;
				}
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.ItemEdition old) {
					new ItemEdition(old.getPlayer(), old.getEdited()).open();
					return;
				}
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.ParticlesEdition old) {
					new ParticlesEdition(old.getPlayer(), old.getEdited()).open();
					return;
				}
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.RevisionInventory old) {
					new RevisionInventory(old.getPlayer(), old.getEdited()).open();
					return;
				}
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.SoundsEdition old) {
					new SoundsEdition(old.getPlayer(), old.getEdited()).open();
					return;
				}
				if (holder instanceof net.Indyuce.mmoitems.gui.edition.UpgradingEdition old) {
					new UpgradingEdition(old.getPlayer(), old.getEdited()).open();
					return;
				}
			} catch (ReflectiveOperationException e) {
				StringWriter sw = new StringWriter();
				try (PrintWriter pw = new PrintWriter(sw)) {
					e.printStackTrace(pw);
				}
				plugin.getLogger().warning(sw.toString());
				player.sendMessage(ChatColor.RED + "Internal Error.");
			}
		}
	}

	@EventHandler
	public void a(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();

		if (event.getInventory().getHolder() instanceof InjectedInventory inventory) {

			// if inventory is edition inventory
			// then the player can click specific items
			if (!(inventory instanceof EditionInventory) || event.getInventory() != event.getClickedInventory() || !MMOUtils.isMetaItem(item, false))
				return;

			if (ItemTag.has(item, "edition_get_the_item")) {

				// simply give the item if left click
				if (event.getAction() == InventoryAction.PICKUP_ALL) {
					for (ItemStack drop : player.getInventory().addItem(event.getInventory().getItem(4)).values())
						player.getWorld().dropItemNaturally(player.getLocation(), drop);

					// this refreshes the item if it's unstackable
					if (NBTItem.get(event.getInventory().getItem(4)).getBoolean("MMOITEMS_UNSTACKABLE")) {
						((EditionInventory) inventory).updateCachedItem();
						event.getInventory().setItem(4, ((EditionInventory) inventory).getCachedItem());
					}
				}

				// reroll stats if right click
				else if (event.getAction() == InventoryAction.PICKUP_HALF) {
					for (ItemStack drop : player.getInventory().addItem(event.getInventory().getItem(4)).values())
						player.getWorld().dropItemNaturally(player.getLocation(), drop);

					((EditionInventory) inventory).updateCachedItem();
					event.getInventory().setItem(4, ((EditionInventory) inventory).getCachedItem());
				}
			}

			MMOItemTemplate template = ((EditionInventory) inventory).getEdited();
			if (ItemTag.has(item, "edition_back")) {

				// Open the Item Browser yes
				if (inventory instanceof ItemEdition) { new ItemBrowser(player, template.getType()).open(); }

				// Open the RECIPE TYPE BROWSER stat thing
				else if ((inventory instanceof RecipeListGUI)) { new RecipeTypeListGUI(player, template).open((EditionInventory) inventory); }

				// Open the RECIPE LIST thing
				else if ((inventory instanceof RecipeEditorGUI)) { new RecipeListGUI(player, template, ((RecipeEditorGUI) inventory).getRecipeRegistry()).open((EditionInventory) inventory); }

				// Just open the ITEM EDITION I guess
				else { new ItemEdition(player, template).open((EditionInventory) inventory); }
			}
		}
	}
}
