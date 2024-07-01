package net.plastoid501.nci.mixin;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.potion.PotionUtil;
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Component> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.isCreative()) {
            List<Component> list = this.setEffect(stack).getTooltip(client.player, client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            List<Component> list2 = Lists.newArrayList(list);

            int row = this.getItemNameRow(list2);
            if (row == -1) {
                return;
            }
            list2.remove(row);
            list2.add(row, (new TranslatableComponent(Items.SUSPICIOUS_STEW.getTranslationKey())).copy().setStyle(new Style()).applyFormat(ChatFormat.WHITE));
            int row2 = this.getItemIdRow(list2);
            if (client.options.advancedItemTooltips && list2.size() - 1 > row2) {
                list2.remove(row2);
                list2.remove(row2);
            }
            tooltip.clear();
            tooltip.addAll(list2);
        }

    }

    private ItemStack setEffect(ItemStack itemStack) {
        ItemStack itemStack2 = new ItemStack(Items.POTION);
        Collection<StatusEffectInstance> list = new ArrayList<>();
        CompoundTag nbtCompound = itemStack.getTag();
        if (nbtCompound != null && nbtCompound.containsKey("Effects", 9)) {
            ListTag nbtList = nbtCompound.getList("Effects", 10);

            for(int i = 0; i < nbtList.size(); ++i) {
                int j = 160;
                CompoundTag nbtCompound2 = nbtList.getCompoundTag(i);
                if (nbtCompound2.containsKey("EffectDuration", 3)) {
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

    private int getItemNameRow(List<Component> list) {
        for (int i = 0; i < list.size(); i++) {
            Component text = list.get(i);
            if (text.getString().equals(new TranslatableComponent("item.minecraft.potion.effect.empty").getString())) {
                return i;
            }
        }
        return -1;
    }

    private int getItemIdRow(List<Component> list) {
        return list.indexOf(new TextComponent(Registry.ITEM.getId(Items.POTION).toString()).copy().applyFormat(ChatFormat.DARK_GRAY));
    }
}
