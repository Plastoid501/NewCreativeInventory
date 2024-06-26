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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.isCreative()) {
            List<Text> list = this.setEffect(stack).getTooltip(client.player, client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            List<Text> list2 = Lists.newArrayList(list);
            int row = this.getItemNameRow(list2);
            list2.remove(row);
            list2.add(row, (new TranslatableText(Items.SUSPICIOUS_STEW.getTranslationKey())).copy().setStyle(Style.EMPTY).formatted(Formatting.WHITE));
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
        NbtCompound nbtCompound = itemStack.getNbt();
        if (nbtCompound != null && nbtCompound.contains("Effects", 9)) {
            NbtList nbtList = nbtCompound.getList("Effects", 10);

            for(int i = 0; i < nbtList.size(); ++i) {
                int j = 160;
                NbtCompound nbtCompound2 = nbtList.getCompound(i);
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
        Text text = Text.of("");
        text.getSiblings().add(new TranslatableText("item.minecraft.potion.effect.empty"));
        text = text.shallowCopy().fillStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.WHITE)));
        return list.indexOf(text);
    }

    private int getItemIdRow(List<Text> list) {
        return list.indexOf(Text.of(Registry.ITEM.getId(Items.POTION).toString()).shallowCopy().formatted(Formatting.DARK_GRAY));
    }
}
