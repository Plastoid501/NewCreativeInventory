package net.plastoid501.nci.mixin;

//? if <=1.14.2 {
/*import net.minecraft.ChatFormat;*/
//?}
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
//? if <=1.18.2 {
/*import net.minecraft.entity.decoration.painting.PaintingMotive;*/
//? else {
import net.minecraft.entity.decoration.painting.PaintingVariant;
//?}
import net.minecraft.item.DecorationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//? if <=1.16.1 {
/*import net.minecraft.nbt.CompoundTag;*/
//?} else {
import net.minecraft.nbt.NbtCompound;
//?}
//? if <=1.14.2 {
/*import net.minecraft.network.chat.Component;*/
/*import net.minecraft.network.chat.Style;*/
/*import net.minecraft.network.chat.TranslatableComponent;*/
//?}
//? if <=1.14.2 {
/*import net.minecraft.network.chat.TranslatableComponent;*/
//?} elif <=1.18.2 {
/*import net.minecraft.text.TranslatableText;*/
//?} elif <=1.19.2 {
import net.minecraft.text.Text;
//?}
import net.minecraft.util.Formatting;
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
    public void appendTooltip(ItemStack stack,
                              @Nullable World world,
                              List<
                                      //? if <=1.14.2 {
                                      /*Component*/
                                      //?} else {
                                      Text
                                      //?}
                                      > tooltip,
                              TooltipContext context
    ) {
        super.appendTooltip(stack, world, tooltip, context);
        //? if <=1.16.1 {
        /*CompoundTag nbtCompound = stack.getTag();*/
        //?} else {
        NbtCompound nbtCompound = stack.getNbt();
        //?}

        if (nbtCompound != null && nbtCompound.contains("EntityTag", 10)) {
            //? if <=1.16.1 {
            /*CompoundTag nbtCompound = */
            //?} else {
            NbtCompound nbtCompound2 =
            //?}
                    nbtCompound.getCompound("EntityTag");
            if (nbtCompound2 != null && nbtCompound2.contains(
                    //? if >=1.19 {
                    "variant",
                    //?} else {
                    /*"Motive",*/
                    //?}
                    8)
            ) {
                String title = nbtCompound2.getString(
                        //? if >=1.19 {
                        "variant"
                        //?} else {
                        /*"Motive"*/
                        //?}
                );
                //? if >=1.19 {
                PaintingVariant paintingMotive = Registry.PAINTING_VARIANT.get(new Identifier(title));
                //?} elif <=1.14.2 {
                /*PaintingMotive paintingMotive = Registry.MOTIVE.get(new Identifier(title));*/
                //?} else {
                /*PaintingMotive paintingMotive = Registry.PAINTING_MOTIVE.get(new Identifier(title));*/
                //?}
                title = title.replace(":", ".");
                tooltip.add((
                        //? if <=1.14.2 {
                        /*new TranslatableComponent*/
                        //?} elif <=1.18.2 {
                        /*new TranslatableText*/
                        //?} elif <=1.19.2 {
                        Text.translatable
                        //?}
                                ("painting." + title + ".title"))
                        //? if <=1.14.2 {
                        /*.applyFormat(ChatFormat.YELLOW));*/
                        //?} else {
                        .formatted(Formatting.YELLOW));
                        //?}
                tooltip.add((
                        //? if <=1.14.2 {
                        /*new TranslatableComponent*/
                        //?} elif <=1.18.2 {
                        /*new TranslatableText*/
                        //?} elif <=1.19.2 {
                        Text.translatable
                        //?}
                                ("painting." + title + ".author"))
                        //? if <=1.14.2 {
                        /*.applyFormat(ChatFormat.GRAY));*/
                        //?} else {
                        .formatted(Formatting.GRAY));
                        //?}
                tooltip.add(
                        //? if <=1.14.2 {
                        /*new TranslatableComponent*/
                        //?} elif <=1.18.2 {
                        /*new TranslatableText*/
                        //?} elif <=1.19.2 {
                        Text.translatable
                        //?}
                                ("painting.dimensions", Math.floorDiv(paintingMotive.getWidth(), 16),Math.floorDiv(paintingMotive.getHeight(), 16)));
            } else {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null && client.player.isCreative()) {
                    tooltip.add((
                            //? if <=1.14.2 {
                            /*new TranslatableComponent*/
                            //?} elif <=1.18.2 {
                            /*new TranslatableText*/
                            //?} elif <=1.19.2 {
                            Text.translatable
                            //?}
                                    ("painting.random"))
                            //? if <=1.14.2 {
                            /*.applyFormat(ChatFormat.GRAY));*/
                            //?} else {
                            .formatted(Formatting.GRAY));
                            //?}
                }
            }
        } else {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.player.isCreative()) {
                tooltip.add((
                        //? if <=1.14.2 {
                        /*new TranslatableComponent*/
                        //?} elif <=1.18.2 {
                        /*new TranslatableText*/
                        //?} elif <=1.19.2 {
                        Text.translatable
                        //?}
                                ("painting.random"))
                        //? if <=1.14.2 {
                        /*.applyFormat(ChatFormat.GRAY));*/
                        //?} else {
                        .formatted(Formatting.GRAY));
                        //?}
            }
        }
    }
}
