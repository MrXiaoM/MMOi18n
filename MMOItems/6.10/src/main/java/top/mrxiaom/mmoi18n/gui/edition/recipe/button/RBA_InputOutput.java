package top.mrxiaom.mmoi18n.gui.edition.recipe.button;

import io.lumine.mythic.lib.api.util.AltChar;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.mmoi18n.gui.edition.recipe.gui.RecipeEditorGUI;

/**
 * Button to switch between Input and Output modes of the station.
 *
 * @author Gunging
 */
public class RBA_InputOutput extends RecipeButtonAction {

    boolean showingInput;

    /**
     * A button of an Edition Inventory. Nice!
     *
     * @param inv The edition inventory this is a button of
     */
    public RBA_InputOutput(@NotNull RecipeEditorGUI inv) {
        super(inv);

        // By default, input is shown.
        showingInput = true;
    }

    @Override
    public boolean runPrimary() {
        getInv().switchInput();
        getInv().refreshInventory();
        clickSFX();
        return true;
    }

    /**
     * This method never runs.
     *
     * @param message Input from the user
     * @param info Additional objects, specific to each case, provided.
     *
     * @throws IllegalArgumentException Never
     */
    @Override public void primaryProcessInput(@NotNull String message, Object... info) throws IllegalArgumentException { }

    /**
     * Nothing happens
     */
    @Override public boolean runSecondary() { return false; }


    /**
     * This method never runs.
     *
     * @param message Input from the user
     * @param info Additional objects, specific to each case, provided.
     *
     * @throws IllegalArgumentException Never
     */
    @Override public void secondaryProcessInput(@NotNull String message, Object... info) throws IllegalArgumentException { }

    @NotNull final ItemStack button = ItemFactory.of(Material.CRAFTING_TABLE).name("§cSwitch to Output Mode").lore(SilentNumbers.chop(
            "INPUT is the ingredients of the recipe, but (like milk buckets when crafting a cake) these ingredients may not be entirely consumed. In such cases, use the OUTPUT mode to specify what the ingredients will turn into."
            , 63, "§7")).build();

    @NotNull
    @Override
    public ItemStack getButton() {

        // Dictate the correct one
        String input = getInv().isShowingInput() ? "§6INPUT" : "§3OUTPUT";

        // Copy and send
        return RecipeEditorGUI.addLore(button.clone(), SilentNumbers.toArrayList("§7Currently Showing: " + input, "",
                ChatColor.YELLOW + AltChar.listDash + " Left click to switch mode." ));
    }
}
