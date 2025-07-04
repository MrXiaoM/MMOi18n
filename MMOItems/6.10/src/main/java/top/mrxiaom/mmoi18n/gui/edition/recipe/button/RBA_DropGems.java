package top.mrxiaom.mmoi18n.gui.edition.recipe.button;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.mmoi18n.gui.edition.recipe.button.type.RBA_BooleanButton;
import top.mrxiaom.mmoi18n.gui.edition.recipe.gui.RecipeEditorGUI;

/**
 * Will the extra gems (that didn't fit in the other item)
 * drop to the ground (as opposed to being lost)?
 *
 * @author Gunging
 */
public class RBA_DropGems extends RBA_BooleanButton {

    /**
     * A button of an Edition Inventory. Nice!
     *
     * @param inv The edition inventory this is a button of
     */
    public RBA_DropGems(@NotNull RecipeEditorGUI inv) { super(inv); }

    public static final String SMITH_GEMS = "drop-gems";
    @NotNull @Override public String getBooleanConfigPath() { return SMITH_GEMS; }

    @NotNull final ItemStack booleanButton = ItemFactory.of(Material.EMERALD).name("§aDrop Gemstones").lore(SilentNumbers.chop(
            "Usually, gemstones that dont fit the new item are lost. Enable this to make them drop (and be recovered) instead."
            , 65, "§7")).build();
    @NotNull @Override public ItemStack getBooleanButton() { return booleanButton; }
}
