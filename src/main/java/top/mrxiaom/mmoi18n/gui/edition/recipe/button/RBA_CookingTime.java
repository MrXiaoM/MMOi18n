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

public class RBA_CookingTime extends RBA_DoubleButton {

    /**
     * A button of an Edition Inventory. Nice!
     *
     * @param inv The edition inventory this is a button of
     */
    public RBA_CookingTime(@NotNull RecipeEditorGUI inv) { super(inv); }

    public static final String FURNACE_TIME = "time";
    @NotNull @Override public String getDoubleConfigPath() { return FURNACE_TIME; }

    @Nullable @Override public QuickNumberRange getRange() { return new QuickNumberRange(0D, null); }

    @Override public boolean requireInteger() { return true; }

    public static final double DEFAULT = 200;
    @Override public double getDefaultValue() { return DEFAULT; }

    @NotNull final ItemStack doubleButton = ItemFactory.of(Material.CLOCK).name("\u00a7cDuration").lore(SilentNumbers.chop(
            "How long it takes this recipe to finish cooking"
            , 65, "\u00a77")).build();
    @NotNull @Override public ItemStack getDoubleButton() { return doubleButton; }
}
