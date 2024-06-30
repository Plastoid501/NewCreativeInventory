package net.plastoid501.nci.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

import static net.minecraft.block.LightBlock.LEVEL_15;

public class NewItemGroup {
    private final String id;
    private final int index;
    private final Text displayName;
    private final ItemStack icon;
    private final Collection<ItemStack> items;

    public NewItemGroup(String id, int index, Text displayName, ItemStack icon) {
        this.id = id;
        this.index = index;
        this.displayName = displayName;
        this.icon = icon;
        this.items = new LinkedHashSet<>();
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public Text getDisplayName() {
        return displayName;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public Collection<ItemStack> getItems() {
        return items;
    }

    public String getTexture() {
        switch (this.id) {
            case "inventory" -> {
                return "inventory.png";
            }
            case "search" -> {
                return "item_search.png";
            }
            default -> {
                return "items.png";
            }
        }
    }

    public int getColumn() {
        return this.index % 7;
    }

    public boolean isTopRow() {
        return this.index < 7;
    }

    public boolean isSpecial() {
        return this.getColumn() >= 5;
    }

    public void appendStacks(DefaultedList<ItemStack> list) {
        list.addAll(this.items);
    }

    public void appendStacksWithoutSameItemStack(DefaultedList<ItemStack> list, ItemStack itemStack) {
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(itemStack);
            if (enchantments.keySet().size() != 1) {
                return;
            }
            for (Enchantment enchantment : enchantments.keySet()) {
                for (int lvl = enchantment.getMinLevel(); lvl <= enchantment.getMaxLevel(); lvl++) {
                    list.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, lvl)));
                }
            }
        } else if (!this.containsItemStack(list, itemStack)) {
            list.add(itemStack);
        }
    }

    private boolean containsItemStack(DefaultedList<ItemStack> list, ItemStack itemStack) {
        for (ItemStack itemStack2 : list) {
            if (ItemStack.areEqual(itemStack, itemStack2)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsItemStack(Collection<ItemStack> list, ItemStack itemStack) {
        for (ItemStack itemStack2 : list) {
            if (ItemStack.areItemsEqual(itemStack, itemStack2)) {
                return true;
            }
        }
        return false;
    }

    public void add(Item item) {
        this.items.add(new ItemStack(item));
    }

    public void add(ItemStack stack) {
        this.items.add(stack);
    }

    public void addPaintings() {
        for (PaintingMotive paintingMotive : Registry.PAINTING_MOTIVE) {
            ItemStack itemStack = new ItemStack(Items.PAINTING);
            NbtCompound nbt = itemStack.getOrCreateSubNbt("EntityTag");
            nbt.putString("Motive", Registry.PAINTING_MOTIVE.getId(paintingMotive).toString());
            this.add(itemStack);
        }
    }

    public void addFireworkRockets() {
        byte[] flight_level = new byte[]{1, 2, 3};

        for (byte level : flight_level) {
            ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
            itemStack.getOrCreateSubNbt("Fireworks").putByte("Flight", level);
            this.add(itemStack);
        }

    }

    public void addTippedArrow() {
        this.addPotions(Items.TIPPED_ARROW);
    }

    /*
    public void addInstruments() {
        for (RegistryEntry<Instrument> instrument : Registry.INSTRUMENT.getIndexedEntries()) {
            ItemStack itemStack = GoatHornItem.getStackForInstrument(Items.GOAT_HORN, instrument);
            this.add(itemStack);
        }
    }
     */

    public void addSuspiciousStews() {
        ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(itemStack, StatusEffects.SATURATION, 7);
        this.add(itemStack);
        itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(itemStack, StatusEffects.NIGHT_VISION, 100);
        this.add(itemStack);
        itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(itemStack, StatusEffects.FIRE_RESISTANCE, 80);
        this.add(itemStack);
        itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(itemStack, StatusEffects.BLINDNESS, 160);
        this.add(itemStack);
        itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(itemStack, StatusEffects.WEAKNESS, 180);
        this.add(itemStack);
        itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(itemStack, StatusEffects.RESISTANCE, 160);
        this.add(itemStack);
        itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(itemStack, StatusEffects.JUMP_BOOST, 120);
        this.add(itemStack);
        itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(itemStack, StatusEffects.POISON, 240);
        this.add(itemStack);
        itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(itemStack, StatusEffects.WITHER, 160);
        this.add(itemStack);
    }

    public void addPotions(Item item) {
        for (Potion potion : Registry.POTION) {
            if (potion == Potions.EMPTY) {
                continue;
            }
            this.add(PotionUtil.setPotion(new ItemStack(item), potion));
        }
    }

    public void addMaxLevelEnchantedBooks() {
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            this.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, enchantment.getMaxLevel())));
        }
    }

    public void addAllLevelEnchantedBooks() {
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            for (int lvl = enchantment.getMinLevel(); lvl <= enchantment.getMaxLevel(); lvl++) {
                this.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, lvl)));
            }
        }
    }

    public void addLightBlocks() {
        for(int i = 15; i >= 0; --i) {
            ItemStack itemStack = new ItemStack(Items.LIGHT);
            if (i != 15) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString(LEVEL_15.getName(), String.valueOf(i));
                itemStack.setSubNbt("BlockStateTag", nbtCompound);
            }
            this.add(itemStack);
        }
    }

}
