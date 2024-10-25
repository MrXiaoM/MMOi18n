package top.mrxiaom.mmoi18n.gui;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.inventory.ItemStack;

public class ItemTag {
    public static void put(ItemStack item, String tag) {
        NBT.modify(item, nbt -> {
            nbt.setBoolean(tag, true);
        });
    }

    public static boolean has(ItemStack item, String tag) {
        return NBT.get(item, nbt -> {
            return nbt.hasTag(tag);
        });
    }
}
