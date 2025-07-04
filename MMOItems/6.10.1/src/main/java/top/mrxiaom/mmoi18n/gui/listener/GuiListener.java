package top.mrxiaom.mmoi18n.gui.listener;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.gui.Navigator;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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

	private void handleError(CommandSender sender, Exception e) {
		StringWriter sw = new StringWriter();
		try (PrintWriter pw = new PrintWriter(sw)) {
			e.printStackTrace(pw);
		}
		plugin.getLogger().warning(sw.toString());
		sender.sendMessage(ChatColor.RED + "Internal Error.");
	}

	@EventHandler
	public void a(InventoryOpenEvent event) {
		HumanEntity player = event.getPlayer();
		Inventory inventory = event.getInventory();
		InventoryHolder holder = inventory.getHolder();
		if (holder instanceof InjectedInventory) return;
		if (holder instanceof net.Indyuce.mmoitems.gui.ItemBrowser old) {
			event.setCancelled(true);
			new ItemBrowser(plugin, old.getNavigator(), old.getType()).open();
			return;
		}
		if (holder instanceof net.Indyuce.mmoitems.gui.edition.EditionInventory) {
			event.setCancelled(true);
			if (holder instanceof net.Indyuce.mmoitems.gui.edition.AbilityEdition old) try {
				Field field = old.getClass().getDeclaredField("configKey");
				field.setAccessible(true);
				new AbilityEdition(old.getNavigator(), old.getEdited(), (String) field.get(old)).open();
			} catch (ReflectiveOperationException e) {
				handleError(player, e);
			} else if (holder instanceof net.Indyuce.mmoitems.gui.edition.AbilityListEdition old) {
				new AbilityListEdition(old.getNavigator(), old.getEdited()).open();
			} else if (holder instanceof net.Indyuce.mmoitems.gui.edition.ArrowParticlesEdition old) {
				new ArrowParticlesEdition(old.getNavigator(), old.getEdited()).open();
			} else if (holder instanceof net.Indyuce.mmoitems.gui.edition.CommandListEdition old) {
				new CommandListEdition(old.getNavigator(), old.getEdited()).open();
			} else if (holder instanceof net.Indyuce.mmoitems.gui.edition.ElementsEdition old) {
				new ElementsEdition(old.getNavigator(), old.getEdited()).open();
			} else if (holder instanceof net.Indyuce.mmoitems.gui.edition.ItemEdition old) {
				new ItemEdition(plugin, old.getNavigator(), old.getEdited()).open();
			} else if (holder instanceof net.Indyuce.mmoitems.gui.edition.ParticlesEdition old) {
				new ParticlesEdition(old.getNavigator(), old.getEdited()).open();
			} else if (holder instanceof net.Indyuce.mmoitems.gui.edition.RevisionInventory old) {
				new RevisionInventory(old.getNavigator(), old.getEdited()).open();
			} else if (holder instanceof net.Indyuce.mmoitems.gui.edition.SoundsEdition old) {
				new SoundsEdition(old.getNavigator(), old.getEdited()).open();
			} else if (holder instanceof net.Indyuce.mmoitems.gui.edition.UpgradingEdition old) {
				new UpgradingEdition(old.getNavigator(), old.getEdited()).open();
			}
		}
	}

	@EventHandler
	public void a(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if (event.getInventory().getHolder() instanceof EditionInventory inv
				&& event.getWhoClicked() instanceof Player player
				&& event.getInventory() == event.getClickedInventory()
				&& MMOUtils.isMetaItem(item, false)) {
			if (ItemTag.has(item, "edition_get_the_item")) {
				// simply give the item if left click
				if (event.getAction() == InventoryAction.PICKUP_ALL) {
					for (ItemStack drop : player.getInventory().addItem(event.getInventory().getItem(4)).values()) {
						player.getWorld().dropItemNaturally(player.getLocation(), drop);
					}
					// this refreshes the item if it's not stackable
					if (NBTItem.get(event.getInventory().getItem(4)).getBoolean("MMOITEMS_UNSTACKABLE")) {
						inv.updateCachedItem();
						event.getInventory().setItem(4, inv.getCachedItem());
					}
				}
				// re-roll stats if right click
				else if (event.getAction() == InventoryAction.PICKUP_HALF) {
					for (ItemStack drop : player.getInventory().addItem(event.getInventory().getItem(4)).values()) {
						player.getWorld().dropItemNaturally(player.getLocation(), drop);
					}
					inv.updateCachedItem();
					event.getInventory().setItem(4, inv.getCachedItem());
				}
			}
			Navigator navigator = inv.getNavigator();
			MMOItemTemplate template = inv.getEdited();
			if (ItemTag.has(item, "edition_back")) {
				// Open the Item Browser yes
				if (inv instanceof ItemEdition)
					new ItemBrowser(plugin, navigator, template.getType()).open();
					// Open the RECIPE TYPE BROWSER stat thing
				else if ((inv instanceof RecipeListGUI))
					new RecipeTypeListGUI(navigator, template).open(inv);
					// Open the RECIPE LIST thing
				else if ((inv instanceof RecipeEditorGUI))
					new RecipeListGUI(navigator, template, ((RecipeEditorGUI) inv).getRecipeRegistry()).open(inv);
					// Just open the ITEM EDITION I guess
				else new ItemEdition(plugin, navigator, template).open(inv);
			}
		}
	}
}
