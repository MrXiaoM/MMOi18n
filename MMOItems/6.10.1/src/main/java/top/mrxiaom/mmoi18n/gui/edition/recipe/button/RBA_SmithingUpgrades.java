package top.mrxiaom.mmoi18n.gui.edition.recipe.button;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.api.crafting.recipe.SmithingCombinationType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.mmoi18n.gui.edition.recipe.button.type.RBA_ChooseableButton;
import top.mrxiaom.mmoi18n.gui.edition.recipe.gui.RecipeEditorGUI;

import java.util.ArrayList;

/**
 * Which behaviour do Upgrades follow when the player smiths items?
 *
 * @author Gunging
 */
public class RBA_SmithingUpgrades extends RBA_ChooseableButton {
    /**
     * A button of an Edition Inventory. Nice!
     *
     * @param inv The edition inventory this is a button of
     */
    public RBA_SmithingUpgrades(@NotNull RecipeEditorGUI inv) { super(inv); }

    @NotNull final ItemStack chooseableButton = ItemFactory.of(Material.ANVIL).name("§aUpgrades Transfer").lore(SilentNumbers.chop(
            "What will happen to the upgrades of the ingredients? Will upgraded ingredients produce an upgraded output item?"
            , 65, "§7")).build();

    @NotNull @Override public ItemStack getChooseableButton() { return chooseableButton; }

    public static final String SMITH_UPGRADES = "upgrades";
    @NotNull @Override public String getChooseableConfigPath() { return SMITH_UPGRADES; }
    @NotNull @Override public ArrayList<String> getChooseableList() { return getSmithingList(); }
    @NotNull @Override public String getDefaultValue() { return SmithingCombinationType.MAXIMUM.toString(); }
    @SuppressWarnings("UnnecessaryDefault")
    @NotNull @Override public String getChooseableDefinition(@NotNull String ofChooseable) {
        SmithingCombinationType sct = SmithingCombinationType.MAXIMUM;
        try { sct = SmithingCombinationType.valueOf(getCurrentChooseableValue()); } catch (IllegalArgumentException ignored) {}

        return switch (sct) {
            case EVEN -> "Will take the average of the upgrade levels of the combined items.";
            case NONE -> "Will ignore the upgrade levels of any ingredients.";
            case MAXIMUM -> "Output will have the upgrade level of the most upgraded ingredient.";
            case MINIMUM -> "Output will have the upgrade level of the least-upgraded upgradeable ingredient.";
            case ADDITIVE -> "The upgrade levels of the ingredients will be added, and the result will be the crafted item's level.";
            default -> "Unknown behaviour. Add description in net.Indyuce.mmoitems.gui.edition.recipe.rba.RBA_SmithingUpgrades";
        };
    }

    static ArrayList<String> smithingList;
    /**
     * @return The allowed values of the smithing combination type list
     */
    @NotNull static ArrayList<String> getSmithingList() {
        if (smithingList != null) { return smithingList; }
        smithingList = new ArrayList<>();
        for (SmithingCombinationType sct : SmithingCombinationType.values()) { smithingList.add(sct.toString()); }
        return smithingList; }
}
