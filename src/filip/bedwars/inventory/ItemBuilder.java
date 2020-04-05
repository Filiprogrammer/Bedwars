package filip.bedwars.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
	public static ItemStack NULL = new ItemBuilder().spacer().build();

    private Material material;
    private int amount = 1;
    private Color color;
    private boolean unbreakable;
    private List<String> lore;
    private List<ItemFlag> itemFlags;
    private String name;
    private Map<Enchantment, Integer> enchantments;
    private SkullMeta skullMeta;

    public ItemBuilder() {
        this.material = Material.BARRIER;
        this.amount = 1;
        this.unbreakable = false;
        this.name = null;
        this.color = null;
        this.skullMeta = null;
        this.enchantments = new HashMap<>();
        this.lore = new ArrayList<>();
        this.itemFlags = new ArrayList<>();
    }

    public ItemBuilder setSkullMeta(SkullMeta skullMeta) {
        this.skullMeta = skullMeta;
        return this;
    }

    public ItemBuilder addFlag(ItemFlag flag) {
        if (itemFlags != null) {
            itemFlags.add(flag);
        }
        return this;
    }

    public ItemBuilder addLore(String string) {
        if (lore != null) {
            lore.add(string);
        }
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        if (enchantments != null) {
            enchantments.put(enchantment, level);
        }
        return this;
    }

    public ItemBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder glow() {
        this.itemFlags.add(ItemFlag.HIDE_ENCHANTS);
        this.addEnchantment(Enchantment.DEPTH_STRIDER, 1);
        return this;
    }

    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public ItemBuilder setItemFlags(List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemStack build() {
        if (material == null)
            return null;
        
        ItemStack itemStack = new ItemStack(material, amount);

        if (material == Material.LEGACY_SKULL_ITEM)
            itemStack.setItemMeta(skullMeta);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemFlags != null) {
            for (ItemFlag flag : itemFlags) {
                itemMeta.addItemFlags(flag);
            }
        }

        if (lore != null)
            itemMeta.setLore(lore);

        if (name != null)
            itemMeta.setDisplayName(name);

        if (unbreakable)
        	itemMeta.setUnbreakable(true);

        if (itemMeta instanceof LeatherArmorMeta && color != null) {
            ((LeatherArmorMeta) itemMeta).setColor(color);
        }

        if (enchantments != null) {
            for (Enchantment enchantment : enchantments.keySet()) {
                itemMeta.addEnchant(enchantment, enchantments.get(enchantment), true);
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemBuilder spacer() {
        this.material = Material.GRAY_STAINED_GLASS_PANE;
        this.amount = 1;
        this.name = "§r";
        return this;
    }
    
    public Material getMaterial() {
    	return material;
    }
    
    public int getAmount() {
    	return amount;
    }
    
    public Color getColor() {
    	return color;
    }
    
    public boolean getUnbreakable() {
    	return unbreakable;
    }
    
    public List<String> getLore() {
    	return lore;
    }
    
    public List<ItemFlag> getItemFlags() {
    	return itemFlags;
    }
    
    public String getName() {
    	return name;
    }
    
    public Map<Enchantment, Integer> getEnchantments() {
    	return enchantments;
    }
    
    public SkullMeta getSkullMeta() {
    	return skullMeta;
    }
}
