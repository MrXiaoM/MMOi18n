package top.mrxiaom.mmoi18n.gui.edition.recipe.button;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.mmoi18n.gui.edition.recipe.button.type.RBA_DoubleButton;
import top.mrxiaom.mmoi18n.gui.edition.recipe.gui.RecipeEditorGUI;

/**
 * Experience from furnace recipes and stuff
 *
 * @author Gunging
 */
public class RBA_Experience extends RBA_DoubleButton {

    /**
     * A button of an Edition Inventory. Nice!
     *
     * @param inv The edition inventory this is a button of
     */
    public RBA_Experience(@NotNull RecipeEditorGUI inv) { super(inv); }

    public static final String FURNACE_EXPERIENCE = "exp";
    @NotNull @Override public String getDoubleConfigPath() { return FURNACE_EXPERIENCE; }

    @Nullable @Override public QuickNumberRange getRange() { return new QuickNumberRange(0D, null); }

    @Override public boolean requireInteger() { return false; }

    public static final double DEFAULT = 0.35;
    @Override public double getDefaultValue() { return DEFAULT; }

    @NotNull final ItemStack doubleButton = ItemFactory.of(Material.EXPERIENCE_BOTTLE).name("§aExperience").lore(SilentNumbers.chop(
            "This recipe gives experience when crafted, how much?"
            , 65, "§7")).build();
    @NotNull @Override public ItemStack getDoubleButton() { return doubleButton; }
}
