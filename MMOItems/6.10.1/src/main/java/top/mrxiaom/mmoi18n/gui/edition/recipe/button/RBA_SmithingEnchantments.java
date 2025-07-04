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
 * Which behaviour do Enchantments follow when the player smiths items?
 *
 * @author Gunging
 */
public class RBA_SmithingEnchantments extends RBA_ChooseableButton {

    /**
     * A button of an Edition Inventory. Nice!
     *
     * @param inv The edition inventory this is a button of
     */
    public RBA_SmithingEnchantments(@NotNull RecipeEditorGUI inv) { super(inv); }

    @NotNull final ItemStack chooseableButton = ItemFactory.of(Material.ENCHANTING_TABLE).name("§aEnchantment Transfer").lore(SilentNumbers.chop(
            "What will happen to the enchantments of the ingredients? Will enchanted ingredients produce an enchanted output item?"
            , 65, "§7")).build();

    @NotNull @Override public ItemStack getChooseableButton() { return chooseableButton; }

    public static final String SMITH_ENCHANTS = "enchantments";
    @NotNull @Override public String getChooseableConfigPath() { return SMITH_ENCHANTS; }
    @NotNull @Override public ArrayList<String> getChooseableList() { return RBA_SmithingUpgrades.getSmithingList(); }
    @NotNull @Override public String getDefaultValue() { return SmithingCombinationType.MAXIMUM.toString(); }
    @SuppressWarnings("UnnecessaryDefault")
    @NotNull @Override public String getChooseableDefinition(@NotNull String ofChooseable) {
        SmithingCombinationType sct = SmithingCombinationType.MAXIMUM;
        try { sct = SmithingCombinationType.valueOf(getCurrentChooseableValue()); } catch (IllegalArgumentException ignored) {}

        return switch (sct) {
            case EVEN -> "For each enchantment, will take the average of that enchantment's level across the ingredients.";
            case NONE -> "Will ignore the enchantments of any ingredients.";
            case MAXIMUM -> "Output will have the best enchantment from each ingredient";
            case MINIMUM -> "Output will have worst enchantment from each ingredient with that enchantment.";
            case ADDITIVE -> "The enchantments of all ingredients will add together.";
            default -> "Unknown behaviour. Add description in net.Indyuce.mmoitems.gui.edition.recipe.rba.RBA_SmithingEnchantments";
        };
    }
}
