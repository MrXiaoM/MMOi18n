package top.mrxiaom.mmoi18n.gui.edition.recipe.button.type;

import io.lumine.mythic.lib.api.util.AltChar;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.mmoi18n.gui.edition.recipe.button.RecipeButtonAction;
import top.mrxiaom.mmoi18n.gui.edition.recipe.gui.RecipeEditorGUI;

/**
 * A button that toggles between true and false.
 *
 * @author Gunging
 */
public abstract class RBA_BooleanButton extends RecipeButtonAction {
    /**
     * A button of an Edition Inventory. Nice!
     *
     * @param inv The edition inventory this is a button of
     */
    public RBA_BooleanButton(@NotNull RecipeEditorGUI inv) {
        super(inv);
    }

    /**
     * @return Straight from the file, if this option is set to TRUE.
     */
    public boolean isEnabled() { return getInv().getNameSection().getBoolean(getBooleanConfigPath(), false); }
    /**
     * @return The path to save this value in the config
     */
    @NotNull public abstract String getBooleanConfigPath();

    @Override public boolean runPrimary() {

        // Flip value
        getInv().getNameSection().set(getBooleanConfigPath(), !isEnabled());
        clickSFX();

        /*
         * Register template edition. This is only done automatically
         * on the input process methods, not on the run button ones.
         */
        getInv().registerTemplateEdition();

        // Done
        return true;
    }
    /**
     * The user needs to input nothing; Thus this method never runs.
     *
     * @param message Input from the user
     * @param info Additional objects, specific to each case, provided.
     *
     * @throws IllegalArgumentException Never
     */
    @Override public void primaryProcessInput(@NotNull String message, Object... info) throws IllegalArgumentException { }

    @Override public boolean runSecondary() {

        // Remove value
        getInv().getNameSection().set(getBooleanConfigPath(), null);
        clickSFX();

        /*
         * Register template edition. This is only done automatically
         * on the input process methods, not on the run button ones.
         */
        getInv().registerTemplateEdition();

        // Done
        return true; }
    /**
     * The user needs to input nothing; Thus this method never runs.
     *
     * @param message Input from the user
     * @param info Additional objects, specific to each case, provided.
     *
     * @throws IllegalArgumentException Never
     */
    @Override public void secondaryProcessInput(@NotNull String message, Object... info) throws IllegalArgumentException { }

    /**
     * @return The button ItemStack with its name and description. To
     *         it, all the chooseable values will be appended (as well
     *         as the definition of the current value chosen) when asked
     *         for in {@link #getButton()}
     */
    @NotNull public abstract ItemStack getBooleanButton();
    /**
     * @return Same as {@link #getBooleanButton()} but with
     *         the current value information appended to it.
     */
    @NotNull @Override public ItemStack getButton() {
        // Dictate the correct one
        String input = isEnabled() ? "§aTRUE" : "§cFALSE";

        // Copy and send
        return RecipeEditorGUI.addLore(getBooleanButton().clone(),
                SilentNumbers.toArrayList(
                        "", "§7Current Value: " + input, "",
                        ChatColor.YELLOW + AltChar.listDash + " Right click to reset §8(to§4 FALSE§8)§e.",
                        ChatColor.YELLOW + AltChar.listDash + " Left click to toggle this option." ));
    }
}
