package top.mrxiaom.mmoi18n.gui.edition;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.util.AltChar;
import io.lumine.mythic.lib.element.Element;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.stat.data.random.RandomElementListData;
import net.Indyuce.mmoitems.util.ElementStatType;
import net.Indyuce.mmoitems.util.MMOUtils;
import net.Indyuce.mmoitems.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.mrxiaom.mmoi18n.edition.StatEdition;
import top.mrxiaom.mmoi18n.gui.ItemTag;

import java.util.*;

public class ElementsEdition extends EditionInventory {
    private final List<Element> elements = new ArrayList<>();
    private final int maxPage;
    private final Map<Integer, Pair<Element, ElementStatType>> editableStats = new HashMap<>();

    private int page = 1;

    private static final int[] INIT_SLOTS = {19, 28, 37};
    private static final int ELEMENTS_PER_PAGE = 3;

    public ElementsEdition(Player player, MMOItemTemplate template) {
        super(player, template);

        elements.addAll(MythicLib.plugin.getElements().getAll());
        maxPage = 1 + (MythicLib.plugin.getElements().getAll().size() - 1) / ELEMENTS_PER_PAGE;
    }

    @Override
    public String getName() {
        return "元素列表: " + template.getId();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void arrangeInventory() {
        final Optional<RandomElementListData> statData = getEventualStatData(ItemStats.ELEMENTS);

        ItemStack prevPage = new ItemStack(Material.ARROW);
        ItemMeta prevPageMeta = prevPage.getItemMeta();
        if (prevPageMeta != null) prevPageMeta.setDisplayName(ChatColor.GREEN + "上一页");
        prevPage.setItemMeta(prevPageMeta);
        ItemTag.put(prevPage, "prev_page");
        inventory.setItem(25, prevPage);

        ItemStack nextPage = new ItemStack(Material.ARROW);
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        if (nextPageMeta != null) nextPageMeta.setDisplayName(ChatColor.GREEN + "下一页");
        nextPage.setItemMeta(nextPageMeta);
        ItemTag.put(nextPage, "next_page");
        inventory.setItem(43, nextPage);

        editableStats.clear();

        final int startingIndex = (page - 1) * ELEMENTS_PER_PAGE;
        for (int i = 0; i < ELEMENTS_PER_PAGE; i++) {
            final int index = startingIndex + i;
            if (index >= elements.size())
                break;

            Element element = elements.get(index);
            int k = 0;
            for (ElementStatType statType : ElementStatType.values()) {
                ItemStack statItem = new ItemStack(element.getIcon());
                ItemMeta statMeta = statItem.getItemMeta();
                if (statMeta != null) {
                    statMeta.setDisplayName(ChatColor.GREEN + element.getName() + " " + statType.getName());
                    List<String> statLore = new ArrayList<>();
                    statLore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN +
                            (statData.isPresent() && statData.get().hasStat(element, statType)
                                    ? statData.get().getStat(element, statType)
                                    : "---"));
                    statLore.add("");
                    statLore.add(ChatColor.YELLOW + AltChar.listDash + " Click to change this value.");
                    statLore.add(ChatColor.YELLOW + AltChar.listDash + " Right click to remove this value.");
                    statMeta.setLore(statLore);
                }
                statItem.setItemMeta(statMeta);

                final int slot = INIT_SLOTS[i] + k;
                inventory.setItem(slot, statItem);
                editableStats.put(slot, Pair.of(element, statType));
                k++;
            }
        }
    }

    @Override
    public void whenClicked(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();

        event.setCancelled(true);
        if (event.getInventory() != event.getClickedInventory() || !MMOUtils.isMetaItem(item, false))
            return;

        if (page > 1 && ItemTag.has(item, "prev_page")) {
            page--;
            refreshInventory();
            return;
        }

        if (page < maxPage && ItemTag.has(item, "next_page")) {
            page++;
            refreshInventory();
            return;
        }

        Pair<Element, ElementStatType> edited = editableStats.get(event.getSlot());
        if (edited == null)
            return;

        final String elementPath = edited.getValue().getConcatenatedConfigPath(edited.getKey());

        if (event.getAction() == InventoryAction.PICKUP_ALL)
            new StatEdition(this, ItemStats.ELEMENTS, elementPath).enable("Write in the value you want.");

        else if (event.getAction() == InventoryAction.PICKUP_HALF) {
            getEditedSection().set("element." + elementPath, null);

            // Clear element config section
            String elementName = edited.getKey().getId();
            ConfigurationSection section = getEditedSection().getConfigurationSection("element." + elementName);
            if (section != null && section.getKeys(false).isEmpty()) {
                getEditedSection().set("element." + elementName, null);

                ConfigurationSection section1 = getEditedSection().getConfigurationSection("element");
                if (section1 != null && section1.getKeys(false).isEmpty())
                    getEditedSection().set("element", null);
            }

            registerTemplateEdition();
            player.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + edited.getKey().getName() + " " + edited.getValue().getName() + ChatColor.GRAY + " successfully removed.");
        }
    }
}