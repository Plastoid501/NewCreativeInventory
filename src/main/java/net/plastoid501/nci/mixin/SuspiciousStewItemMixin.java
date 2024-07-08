package net.plastoid501.nci.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;

import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Style;
//? if <=1.16.1 {
/*import net.minecraft.nbt.CompoundTag;*/
/*import net.minecraft.nbt.ListTag;*/
//?} else {
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
//?}
//? if <=1.14.2 {
/*import net.minecraft.network.chat.Component;*/
/*import net.minecraft.network.chat.Style;*/
/*import net.minecraft.network.chat.TranslatableComponent;*/
//?} elif <=1.18.2 {
/*import net.minecraft.text.TranslatableText;*/
//?} elif <=1.19.2 {
import net.minecraft.text.Text;
//?}
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(SuspiciousStewItem.class)
public class SuspiciousStewItemMixin extends Item {

    public SuspiciousStewItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack,
                              @Nullable World world,
                              List<
                                      //? if <=1.14.2 {
                                      /*Component*/
                                      //?} else {
                                      Text
                                      //?}
                                      > tooltip,
                              TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.isCreative()) {
            List<
                    //? if <=1.14.2 {
                    /*Component*/
                    //?} else {
                    Text
                    //?}
                    > list = this.setEffect(stack).getTooltip(client.player, client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            List<
                    //? if <=1.14.2 {
                    /*Component*/
                    //?} else {
                    Text
                    //?}
                    > list2 = Lists.newArrayList(list);
            int row = this.getItemNameRow(list2);
            if (row == -1) {
                return;
            }
            list2.remove(row);
            list2.add(row, (
                    //? if <=1.14.2 {
                    /*new TranslatableComponent*/
                    //?} elif <=1.18.2 {
                    /*new TranslatableText*/
                    //?} elif <=1.19.2 {
                    Text.translatable
                    //?}
                            (Items.SUSPICIOUS_STEW.getTranslationKey()))
                    .copy()
                    .setStyle(
                            //? if <=1.15.2 {
                            /*new Style()*/
                            //?} else {
                            Style.EMPTY
                            //?}
                    )
                    //? if <=1.14.2 {
                    /*.applyFormat(ChatFormat.WHITE));*/
                    //?} else {
                    .formatted(Formatting.WHITE));
                    //?}
            int row2 = this.getItemIdRow(list2);
            if (client.options.advancedItemTooltips && list2.size() - 1 > row2) {
                list2.remove(row2);
                list2.remove(row2);
            }
            tooltip.clear();
            tooltip.addAll(list2);
        }

    }

    private ItemStack setEffect(ItemStack stack) {
        ItemStack itemStack2 = new ItemStack(Items.POTION);
        Collection<StatusEffectInstance> list = new ArrayList<>();
        //? if <=1.16.1 {
        /*CompoundTag nbtCompound = stack.getTag();*/
        //?} else {
        NbtCompound nbtCompound = stack.getNbt();
        //?}

        if (nbtCompound != null && nbtCompound.contains("Effects", 9)) {
            //? if <=1.16.1 {
            /*ListTag nbtList = */
            //?} else {
            NbtList nbtList =
            //?}
                    nbtCompound.getList("Effects", 10);
            for(int i = 0; i < nbtList.size(); ++i) {
                int j = 160;
                //? if <=1.16.1 {
                /*CompoundTag nbtCompound = */
                //?} else {
                NbtCompound nbtCompound2 =
                //?}
                        nbtList.getCompound(i);

                if (nbtCompound2.contains("EffectDuration", 3)) {
                    j = nbtCompound2.getInt("EffectDuration");
                }

                StatusEffect statusEffect = StatusEffect.byRawId(nbtCompound2.getByte("EffectId"));
                if (statusEffect != null) {
                    list.add(new StatusEffectInstance(statusEffect, j));
                }
            }
        }
        PotionUtil.setCustomPotionEffects(itemStack2, list);
        return itemStack2;
    }

    private int getItemNameRow(List<Text> list) {
        for (int i = 0; i < list.size(); i++) {
            Text text = list.get(i);
            if (text.getString().equals(
                    //? if <=1.14.2 {
                    /*new TranslatableComponent*/
                    //?} elif <=1.18.2 {
                    /*new TranslatableText*/
                    //?} elif <=1.19.2 {
                    Text.translatable
                    //?}
                            ("item.minecraft.potion.effect.empty").getString())) {
                return i;
            }
        }
        return -1;
    }

    private int getItemIdRow(List<Text> list) {
        return list.indexOf(Text.of(Registry.ITEM.getId(Items.POTION).toString())
                //? if <=1.15.2 {
                /*.copy()*/
                //?} elif <=1.18.2 {
                /*.shallowCopy()*/
                //?} else {
                .copy()
                //?}

                //? if <=1.14.2 {
                /*.applyFormat(ChatFormat.DARK_GRAY));*/
                //?} else {
                .formatted(Formatting.DARK_GRAY));
                //?}
    }
}
