package top.mrxiaom.mmoi18n.gui.edition;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.AltChar;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.particle.api.ParticleType;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import top.mrxiaom.mmoi18n.edition.StatEdition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.mmoi18n.gui.ItemTag.has;
import static top.mrxiaom.mmoi18n.gui.ItemTag.put;

public class ParticlesEdition extends EditionInventory {
    private static final NamespacedKey PATTERN_MODIFIED_KEY = new NamespacedKey(MMOItems.plugin, "PatternModifierId");

    public ParticlesEdition(Player player, MMOItemTemplate template) {
        super(player, template);
    }

    @Override
    public String getName() {
        return "粒子效果: " + template.getId();
    }

    @Override
    public void arrangeInventory() {
        int[] slots = {37, 38, 39, 40, 41, 42, 43};
        int n = 0;

        @Nullable ParticleType particleType = null;
        try {
            particleType = ParticleType.valueOf(getEditedSection().getString("item-particles.type"));
        } catch (Exception ignored) {
        }

        ItemStack particleTypeItem = new ItemStack(Material.PINK_STAINED_GLASS);
        ItemMeta particleTypeItemMeta = particleTypeItem.getItemMeta();
        if (particleTypeItemMeta != null) {
            particleTypeItemMeta.setDisplayName(ChatColor.GREEN + "粒子图案");
            List<String> particleTypeItemLore = new ArrayList<>();
            particleTypeItemLore.add(ChatColor.GRAY + "粒子图案决定了使用何种粒子，");
            particleTypeItemLore.add(ChatColor.GRAY + "以什么图案，在何时显示或形成什么形状.");
            particleTypeItemLore.add("");
            particleTypeItemLore.add(ChatColor.GRAY + "当前值: "
                    + (particleType == null ? ChatColor.RED + "未选择类型." : ChatColor.GOLD + particleType.getDefaultName()));
            if (particleType != null) {
                particleTypeItemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + particleType.getDescription());
            }
            particleTypeItemLore.add("");
            particleTypeItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
            particleTypeItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
            particleTypeItemMeta.setLore(particleTypeItemLore);
        }
        particleTypeItem.setItemMeta(particleTypeItemMeta);
        put(particleTypeItem, "particle_pattern");

        ConfigurationSection section = getEditedSection().getConfigurationSection("item-particles");
        if (particleType != null && section != null) {
            for (String modifier : particleType.getModifiers()) {
                final ItemStack modifierItem = new ItemStack(Material.GRAY_DYE);
                ItemMeta modifierItemMeta = modifierItem.getItemMeta();
                if (modifierItemMeta != null) {
                    modifierItemMeta.setDisplayName(ChatColor.GREEN + UtilityMethods.caseOnWords(modifier.toLowerCase().replace("-", " ")));
                    List<String> modifierItemLore = new ArrayList<>();
                    modifierItemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "这是一个图案修改器.");
                    modifierItemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "更改这个值可以轻微地自定义粒子图案.");
                    modifierItemLore.add("");
                    modifierItemLore.add(ChatColor.GRAY + "当前值: " + ChatColor.GOLD
                            + (section.contains(modifier) ? section.getDouble(modifier) : particleType.getModifier(modifier)));
                    modifierItemLore.add("");
                    modifierItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
                    modifierItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
                    modifierItemMeta.setLore(modifierItemLore);
                    modifierItemMeta.getPersistentDataContainer().set(PATTERN_MODIFIED_KEY, PersistentDataType.STRING, modifier);
                }
                modifierItem.setItemMeta(modifierItemMeta);

                inventory.setItem(slots[n++], modifierItem);
            }
        }

        @Nullable Particle particle = null;
        try {
            particle = Particle.valueOf(getEditedSection().getString("item-particles.particle"));
        } catch (Exception ignored) {
        }

        ItemStack particleItem = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta particleItemMeta = particleItem.getItemMeta();
        if (particleItemMeta != null) {
            particleItemMeta.setDisplayName(ChatColor.GREEN + "粒子");
            List<String> particleItemLore = new ArrayList<>();
            particleItemLore.add(ChatColor.GRAY + "决定在粒子效果中使用何种粒子.");
            particleItemLore.add("");
            particleItemLore.add(ChatColor.GRAY + "当前值: " + (particle == null ? ChatColor.RED + "未选择粒子."
                    : ChatColor.GOLD + UtilityMethods.caseOnWords(particle.name().toLowerCase().replace("_", " "))));
            particleItemLore.add("");
            particleItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
            particleItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
            particleItemMeta.setLore(particleItemLore);
        }
        particleItem.setItemMeta(particleItemMeta);
        put(particleItem, "particle");

        if (particle != null && MMOUtils.isColorable(particle)) {
            int red = getEditedSection().getInt("item-particles.color.red");
            int green = getEditedSection().getInt("item-particles.color.green");
            int blue = getEditedSection().getInt("item-particles.color.blue");

            ItemStack colorItem = new ItemStack(Material.RED_DYE);
            ItemMeta colorItemMeta = colorItem.getItemMeta();
            if (colorItemMeta != null) {
                colorItemMeta.setDisplayName(ChatColor.GREEN + "粒子颜色");
                List<String> colorItemLore = new ArrayList<>();
                colorItemLore.add(ChatColor.GRAY + "你的粒子的 RGB 颜色.");
                colorItemLore.add("");
                colorItemLore.add(ChatColor.GRAY + "当前值 (R-G-B):");
                colorItemLore.add("" + ChatColor.RED + ChatColor.BOLD + red + ChatColor.GRAY + " - " + ChatColor.GREEN + ChatColor.BOLD + green
                        + ChatColor.GRAY + " - " + ChatColor.BLUE + ChatColor.BOLD + blue);
                colorItemLore.add("");
                colorItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 左键 更改数值.");
                colorItemLore.add(ChatColor.YELLOW + AltChar.listDash + " 右键 重置.");
                colorItemMeta.setLore(colorItemLore);
            }
            colorItem.setItemMeta(colorItemMeta);
            put(colorItem, "particle_color");

            inventory.setItem(25, colorItem);
        }

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) glassMeta.setDisplayName(ChatColor.RED + "- 无修改器 -");
        glass.setItemMeta(glassMeta);

        while (n < slots.length)
            inventory.setItem(slots[n++], glass);

        inventory.setItem(21, particleTypeItem);
        inventory.setItem(23, particleItem);
    }

    @Override
    public void whenClicked(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();

        event.setCancelled(true);
        if (event.getInventory() != event.getClickedInventory() || !MMOUtils.isMetaItem(item, false))
            return;

        if (has(item, "particle")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL)
                new StatEdition(this, ItemStats.ITEM_PARTICLES, "particle").enable("请在聊天栏发送粒子类型.");

            if (event.getAction() == InventoryAction.PICKUP_HALF) {
                if (getEditedSection().contains("item-particles.particle")) {
                    getEditedSection().set("item-particles.particle", null);
                    registerTemplateEdition();
                    player.sendMessage(MMOItems.plugin.getPrefix() + "已成功重置粒子类型.");
                }
            }
        }

        if (has(item, "particle_color")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL)
                new StatEdition(this, ItemStats.ITEM_PARTICLES, "particle-color").enable("请在聊天栏发送 RGB 颜色.",
                        ChatColor.AQUA + "格式: [红] [绿] [蓝]");

            if (event.getAction() == InventoryAction.PICKUP_HALF) {
                if (getEditedSection().contains("item-particles.color")) {
                    getEditedSection().set("item-particles.color", null);
                    registerTemplateEdition();
                    player.sendMessage(MMOItems.plugin.getPrefix() + "已成功重置粒子颜色.");
                }
            }
        }

        if (has(item, "particle_pattern")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                new StatEdition(this, ItemStats.ITEM_PARTICLES, "particle-type").enable("请在聊天栏发送你想要的粒子图案.");
                player.sendMessage("");
                player.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "可用的粒子图案如下");
                for (ParticleType type : ParticleType.values())
                    player.sendMessage("* " + ChatColor.GREEN + type.name());
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF) {
                if (getEditedSection().contains("item-particles.type")) {
                    getEditedSection().set("item-particles.type", null);

                    // reset other modifiers
                    ConfigurationSection section = getEditedSection().getConfigurationSection("item-particles");
                    if (section != null) for (String key : section.getKeys(false))
                        if (!key.equals("particle"))
                            getEditedSection().set("item-particles." + key, null);

                    registerTemplateEdition();
                    player.sendMessage(MMOItems.plugin.getPrefix() + "已成功重置粒子图案.");
                }
            }
        }

        ItemMeta meta = item.getItemMeta();
        final String tag = meta == null ? null : meta.getPersistentDataContainer().get(PATTERN_MODIFIED_KEY, PersistentDataType.STRING);
        if (tag == null || tag.isEmpty()) return;

        if (event.getAction() == InventoryAction.PICKUP_ALL)
            new StatEdition(this, ItemStats.ITEM_PARTICLES, tag).enable("请在聊天栏发送数值.");

        if (event.getAction() == InventoryAction.PICKUP_HALF) {
            if (getEditedSection().contains("item-particles." + tag)) {
                getEditedSection().set("item-particles." + tag, null);
                registerTemplateEdition();
                player.sendMessage(MMOItems.plugin.getPrefix() + "已成功重置 " + ChatColor.GOLD + tag + ChatColor.GRAY + ".");
            }
        }
    }
}
