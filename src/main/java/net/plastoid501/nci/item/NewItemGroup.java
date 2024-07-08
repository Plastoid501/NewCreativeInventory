package net.plastoid501.nci.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
//?if <=1.15.2 {
/*import net.minecraft.enchantment.InfoEnchantment;*/
//?} else {
import net.minecraft.enchantment.EnchantmentLevelEntry;
//?}
//? if <=1.18.2 {
/*import net.minecraft.entity.decoration.painting.PaintingMotive;*/
//? else {
import net.minecraft.entity.decoration.painting.PaintingVariant;
//?}
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
//? if <=1.16.1 {
/*import net.minecraft.nbt.CompoundTag;*/
//?} else {
import net.minecraft.nbt.NbtCompound;
//?}
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
//? if <=1.14.2 {
/*import net.minecraft.network.chat.TranslatableComponent;*/
//?} elif <=1.18.2 {
/*import net.minecraft.text.TranslatableText;*/
//?} elif <=1.19.2 {
import net.minecraft.text.Text;
//?}
//?if <=1.15.2 {
/*import net.minecraft.util.DefaultedList;*/
//?} else {
import net.minecraft.util.collection.DefaultedList;
//?}
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

//? if >=1.17 {
import static net.minecraft.block.LightBlock.LEVEL_15;
//?}

public class NewItemGroup {
    private final String id;
    private final int index;
    private final
            //? if <=1.14.2 {
            /*TranslatableComponent*/
            //?} elif <=1.18.2 {
            /*TranslatableText*/
            //?} elif <=1.19.2 {
            Text
            //?}
            displayName;
    private final ItemStack icon;
    private final Collection<ItemStack> items;

    public NewItemGroup(String id,
                        int index,
                        //? if <=1.14.2 {
                        /*TranslatableComponent*/
                        //?} elif <=1.18.2 {
                        /*TranslatableText*/
                        //?} elif <=1.19.2 {
                        Text
                        //?}
                        displayName,
                        ItemStack icon) {
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

    public //? if <=1.14.2 {
            /*TranslatableComponent*/
            //?} elif <=1.18.2 {
            /*TranslatableText*/
            //?} elif <=1.19.2 {
            Text
            //?}
            getDisplayName() {
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
            case("inventory"):
                return "inventory.png";
            case("search"):
                return "item_search.png";
            default:
                return "items.png";
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
        if (
                //? if <=1.16.5 {
                /*itemStack.getItem() == Items.ENCHANTED_BOOK*/
                //?} else {
                itemStack.isOf(Items.ENCHANTED_BOOK)
                //?}
        ) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(itemStack);
            if (enchantments.keySet().size() != 1) {
                return;
            }
            for (Enchantment enchantment : enchantments.keySet()) {
                for (int lvl = enchantment
                        //? if <=1.15.2 {
                        /*.getMinimumLevel()*/
                        //?} else {
                        .getMinLevel();
                        //?}
                     lvl <= enchantment
                             //? if <=1.15.2 {
                             /*.getMaximumLevel()*/
                             //?} else {
                             .getMaxLevel();
                     lvl++) {
                    list.add(EnchantedBookItem.forEnchantment(
                            //?if <=1.15.2 {
                            /*new InfoEnchantment*/
                            //?} else {
                            new EnchantmentLevelEntry
                            //?}
                                    (enchantment, lvl)
                    ));
                }
            }
        } else if (!this.containsItemStack(list, itemStack)) {
            list.add(itemStack);
        }
    }

    private boolean containsItemStack(DefaultedList<ItemStack> list, ItemStack itemStack) {
        for (ItemStack itemStack2 : list) {
            if (ItemStack
                    //? if <=1.15.2 {
                    /*.areEqualIgnoreDamage*/
                    //?} else {
                    .areEqual
                    //?}
                            (itemStack, itemStack2)
            ) {
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
        //? if >=1.19 {
        for (PaintingVariant paintingMotive : Registry.PAINTING_VARIANT) {
        //?} elif <=1.14.2 {
        /*for (PaintingMotive paintingMotive : Registry.MOTIVE) {*/
        //?} else {
        /*for (PaintingMotive paintingMotive : Registry.PAINTING_MOTIVE) {*/
        //?}
            ItemStack itemStack = new ItemStack(Items.PAINTING);
            //? if <=1.16.1 {
            /*CompoundTag nbt = itemStack.getOrCreateSubTag("EntityTag");*/
            //?} else {
            NbtCompound nbt = itemStack.getOrCreateSubNbt("EntityTag");
            //?}
            //? if >=1.19 {
            String title = Registry.PAINTING_VARIANT.getId(paintingMotive).toString();
            boolean bl = this.index == NewItemGroups.SEARCH.getIndex();
            boolean bl2 = this.index == NewItemGroups.OPERATOR.getIndex();
            boolean bl3 = Objects.equals(title, "minecraft:earth") || Objects.equals(title, "minecraft:wind") || Objects.equals(title, "minecraft:water") || Objects.equals(title, "minecraft:fire");
            if (bl || bl2 && bl3 || !bl2 && !bl3) {
                nbt.putString("variant", title);
                this.add(itemStack);
            }
            //?} elif <=1.14.2 {
            /*nbt.putString("Motive", Registry.MOTIVE.getId(paintingMotive).toString());*/
            /*this.add(itemStack);*/
            //?} else {
            /*nbt.putString("Motive", Registry.PAINTING_MOTIVE.getId(paintingMotive).toString());*/
            /*this.add(itemStack);*/
            //?}
        }
    }

    public void addFireworkRockets() {
        byte[] flight_level = new byte[]{1, 2, 3};

        for (byte level : flight_level) {
            ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
            itemStack
                    //? if <=1.16.1 {
                    /*.getOrCreateSubTag*/
                    //?} else {
                    .getOrCreateSubNbt
                    //?}
                            ("Fireworks")
                    .putByte("Flight", level);
            this.add(itemStack);
        }

    }

    public void addTippedArrow() {
        this.addPotions(Items.TIPPED_ARROW);
    }

    public void addInstruments() {
        //? if >=1.19 {
        for (RegistryEntry<Instrument> instrument : Registry.INSTRUMENT.getIndexedEntries()) {
            ItemStack itemStack = GoatHornItem.getStackForInstrument(Items.GOAT_HORN, instrument);
            this.add(itemStack);
        }
        //?}
    }

    public void addSuspiciousStews() {
        this.add(createSuspiciousStew(StatusEffects.SATURATION, 7));
        this.add(createSuspiciousStew(StatusEffects.NIGHT_VISION, 100));
        this.add(createSuspiciousStew(StatusEffects.FIRE_RESISTANCE, 80));
        this.add(createSuspiciousStew(StatusEffects.BLINDNESS, 160));
        this.add(createSuspiciousStew(StatusEffects.WEAKNESS, 180));
        this.add(createSuspiciousStew(StatusEffects.RESISTANCE, 160));
        this.add(createSuspiciousStew(StatusEffects.JUMP_BOOST, 120));
        this.add(createSuspiciousStew(StatusEffects.POISON, 240));
        this.add(createSuspiciousStew(StatusEffects.WITHER, 160));
    }

    private ItemStack createSuspiciousStew(StatusEffect effect, int duration) {
        ItemStack stack = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.addEffectToStew(stack, effect, duration);
        return stack;
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
            this.add(EnchantedBookItem.forEnchantment(
                    //?if <=1.15.2 {
                    /*new InfoEnchantment*/
                    //?} else {
                    new EnchantmentLevelEntry
                    //?}
                            (enchantment, enchantment.getMaxLevel())
            ));
        }
    }

    public void addAllLevelEnchantedBooks() {
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            for (int lvl = enchantment
                    //? if <=1.15.2 {
                    /*.getMinimumLevel()*/
                    //?} else {
                    .getMinLevel();
                //?}
                 lvl <= enchantment
                         //? if <=1.15.2 {
                         /*.getMaximumLevel()*/
                         //?} else {
                         .getMaxLevel();
                 lvl++) {
                this.add(EnchantedBookItem.forEnchantment(
                        //?if <=1.15.2 {
                        /*new InfoEnchantment*/
                        //?} else {
                        new EnchantmentLevelEntry
                        //?}
                                (enchantment, lvl)
                ));
            }
        }
    }

    public void addLightBlocks() {
        //? if >=1.17 {
        for(int i = 15; i >= 0; --i) {
            ItemStack itemStack = new ItemStack(Items.LIGHT);
            if (i != 15) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString(LEVEL_15.getName(), String.valueOf(i));
                itemStack.setSubNbt("BlockStateTag", nbtCompound);
            }
            this.add(itemStack);
        }
        //?}
    }

}
