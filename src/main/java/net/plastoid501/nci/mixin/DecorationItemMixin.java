package net.plastoid501.nci.mixin;

import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(DecorationItem.class)
public class DecorationItemMixin extends Item {
    private final EntityType<? extends AbstractDecorationEntity> entityType;

    public DecorationItemMixin(EntityType<? extends AbstractDecorationEntity> type, Settings settings) {
        super(settings);
        this.entityType = type;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Component> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        CompoundTag nbtCompound = stack.getTag();
        if (nbtCompound != null && nbtCompound.containsKey("EntityTag", 10)) {
            CompoundTag nbtCompound2 = nbtCompound.getCompound("EntityTag");
            if (nbtCompound2 != null && nbtCompound2.containsKey("Motive", 8)) {
                String title = nbtCompound2.getString("Motive");
                PaintingMotive paintingMotive = Registry.MOTIVE.get(new Identifier(title));
                title = title.replace(":", ".");
                tooltip.add((new TranslatableComponent("painting." + title + ".title")).applyFormat(ChatFormat.YELLOW));
                tooltip.add((new TranslatableComponent("painting." + title + ".author")).applyFormat(ChatFormat.GRAY));

                tooltip.add(new TranslatableComponent("painting.dimensions", Math.floorDiv(paintingMotive.getWidth(), 16), Math.floorDiv(paintingMotive.getHeight(), 16)));
            } else {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null && client.player.isCreative()) {
                    tooltip.add((new TranslatableComponent("painting.random")).applyFormat(ChatFormat.GRAY));
                }
            }
        } else {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.player.isCreative()) {
                tooltip.add((new TranslatableComponent("painting.random")).applyFormat(ChatFormat.GRAY));
            }
        }
    }
}
