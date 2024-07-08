package net.plastoid501.nci.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
//? if <=1.14.4 {
/*import com.mojang.blaze3d.platform.GlStateManager;*/
//?} else {
import com.mojang.blaze3d.systems.RenderSystem;
//?}
//? if <=1.14.2 {
/*import net.minecraft.ChatFormat;*/
//?}
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
//? if <=1.14.2 {
/*import net.minecraft.client.render.GuiLighting;*/
//?} elif <=1.14.4 {
/*import net.minecraft.client.render.DiffuseLighting;*/
/*import net.minecraft.client.resource.language.I18n;*/
//?}
import net.minecraft.client.render.GameRenderer;
//? if >1.15.2 {
import net.minecraft.client.util.math.MatrixStack;
//?}
//? if <=1.14.2 {
/*import net.minecraft.entity.player.PlayerEntity;*/
//?} else {
import net.minecraft.entity.player.PlayerInventory;
//?}

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
//? if <=1.15.2 {
/*import net.minecraft.container.Slot;*/
/*import net.minecraft.container.SlotActionType;*/
//?} else {
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
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
//?if <=1.15.2 {
/*import net.minecraft.util.DefaultedList;*/
//?} else {
import net.minecraft.util.collection.DefaultedList;
//?}
import net.plastoid501.nci.item.NewItemGroup;
import net.plastoid501.nci.item.NewItemGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<
        CreativeInventoryScreen
                //? if <=1.15.2
                /*.CreativeContainer*/
                //?} else {
                .CreativeScreenHandler
                //?}
        > {
    @Shadow private static int selectedTab;
    @Shadow private float scrollPosition;
    @Shadow private TextFieldWidget searchBox;
    @Shadow public abstract boolean
            //? if <=1.14.2 {
            /*doRenderScrollBar();*/
            //?} else {
            hasScrollbar();
            //?}
    @Shadow protected abstract void
    //? if <=1.14.4 {
    /*method_2468*/
    //?} else {
    renderTabIcon
    //?}
    (
            //? if >1.15.2 {
            MatrixStack matrices,
            //?}
            ItemGroup group);

    @Unique private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    @Unique private static final int TAB_WIDTH = 26;
    @Unique private static final int TAB_HEIGHT = 32;
    @Unique private static final ItemGroup[] GROUPS;
    @Unique private static final ItemGroup BUILDING_BLOCKS;
    @Unique private static final ItemGroup COLORED_BLOCKS;
    @Unique private static final ItemGroup NATURAL;
    @Unique private static final ItemGroup FUNCTIONAL;
    @Unique private static final ItemGroup REDSTONE;
    @Unique private static final ItemGroup HOTBAR;
    @Unique private static final ItemGroup SEARCH;
    @Unique private static final ItemGroup TOOLS;
    @Unique private static final ItemGroup COMBAT;
    @Unique private static final ItemGroup FOOD_AND_DRINK;
    @Unique private static final ItemGroup INGREDIENTS;
    @Unique private static final ItemGroup SPAWN_EGGS;
    @Unique private static final ItemGroup OPERATOR;
    @Unique private static final ItemGroup INVENTORY2;

    public CreativeInventoryScreenMixin(
            //? if <=1.14.2 {
            /*PlayerEntity playerEntity*/
            //?} else {
            CreativeInventoryScreen
                    //? if <=1.15.2
                    /*.CreativeContainer*/
                    //?} else {
                    .CreativeScreenHandler
                    //?}
                    screenHandler,
            PlayerInventory playerInventory,
            Text text
            //?}
    ) {
        //? if <=1.14.2 {
        /*super(new CreativeInventoryScreen.CreativeContainer(playerEntity), playerEntity.inventory, new TextComponent(""));*/
        //?} else {
        super(screenHandler, playerInventory, text);
        //?}
    }

    @Redirect(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    protected int modifyOnMouseClick1(ItemGroup instance) {
        return NewItemGroups.INVENTORY.getIndex();
    }

    @Inject(method = "onMouseClick", at = @At(
            value = "INVOKE",
            target =
                    //? if <=1.15.2 {
                    /*"Lnet/minecraft/container/PlayerContainer;getStacks()Lnet/minecraft/util/DefaultedList;"*/
                    //?} else {
                    "Lnet/minecraft/screen/PlayerScreenHandler;getStacks()Lnet/minecraft/util/collection/DefaultedList;"
                    //?}
    ))
    private void modifyOnMouseClick2(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (this.client == null || this.client.player == null) {
            return;
        }
        this.client.player.getInventory().clear();
    }

    @Redirect(method = "init()V", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;GROUPS:[Lnet/minecraft/item/ItemGroup;"))
    protected ItemGroup[] modifyInit() {
        return GROUPS;
    }

    @Redirect(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    private int modifyCharTyped(ItemGroup instance) {
        return NewItemGroups.SEARCH.getIndex();
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    private int modifyKeyPressed1(ItemGroup instance) {
        return NewItemGroups.SEARCH.getIndex();
    }

    @Redirect(method = "keyPressed", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;SEARCH:Lnet/minecraft/item/ItemGroup;"))
    private ItemGroup modifyKeyPressed2() {
        return SEARCH;
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*"method_2464",*/
                    //?} else {
                    "search",
                    //?}
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
    private boolean modifySearch(Iterator<Item> instance) {
        for (NewItemGroup itemGroup : NewItemGroups.GROUPS) {
            for (ItemStack itemStack : itemGroup.getItems()) {
                itemGroup.appendStacksWithoutSameItemStack(
                        this
                                //? if <=1.15.2 {
                                /*.container*/
                                //?} else {
                                .handler
                                //?}
                                .itemList,
                        itemStack);
            }
        }
        return false;
    }

    @Redirect(method = "drawForeground", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;GROUPS:[Lnet/minecraft/item/ItemGroup;"))
    protected ItemGroup[] modifyDrawForeground1() {
        return GROUPS;
    }

    @Redirect(method = "drawForeground", at = @At(
            value = "INVOKE",
            target =
                    //? if <=1.16.1 {
                    /*"Lnet/minecraft/item/ItemGroup;hasTooltip()Z"*/
                    //?} else {
                    "Lnet/minecraft/item/ItemGroup;shouldRenderName()Z"
                    //?}
    ))
    protected boolean modifyDrawForeground2(ItemGroup instance) {
        return instance != INVENTORY2;
    }

    @Redirect(method = "drawForeground", at = @At(
            value = "INVOKE",
            target =
                    //? if <=1.17.1 {
                    /*"Lnet/minecraft/item/ItemGroup;getTranslationKey()Ljava/lang/String;"*/
                    //?} else {
                    "Lnet/minecraft/item/ItemGroup;getDisplayName()Lnet/minecraft/text/Text;"
                    //?}
    ))
    protected
    //? if <=1.16.1 {
    /*String*/
    //?} else {
    Text
    //?}
    modifyDrawForeground3(ItemGroup instance) {
        return this.getNewItemGroup(instance).getDisplayName()
                //? if <=1.16.1 {
                /*.getString()*/
                //?}
                ;
    }

    @Redirect(method = "mouseClicked", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;GROUPS:[Lnet/minecraft/item/ItemGroup;"))
    public ItemGroup[] modifyMouseClicked1() {
        return GROUPS;
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    public int modifyMouseClicked2(ItemGroup instance) {
        return NewItemGroups.INVENTORY.getIndex();
    }

    @Redirect(method = "mouseReleased", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;GROUPS:[Lnet/minecraft/item/ItemGroup;"))
    public ItemGroup[] modifyMouseReleased1() {
        return GROUPS;
    }

    @Inject(method = "hasScrollbar", at = @At("HEAD"), cancellable = true)
    private void modifyHasScrollbar(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(
                selectedTab != NewItemGroups.INVENTORY.getIndex() &&
                        //? if <=1.14.4 {
                        /*this.container.method_2474()*/
                        //?} elif <=1.15.2 {
                        /*this.container.shouldShowScrollbar()*/
                        //?} else {
                        this.handler.shouldShowScrollbar()
                        //?}
        );
        cir.cancel();
    }

    @Redirect(method = "setSelectedTab", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;HOTBAR:Lnet/minecraft/item/ItemGroup;"))
    private ItemGroup modifySetSelectedTab1() {
        return HOTBAR;
    }

    @Redirect(method = "setSelectedTab", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;SEARCH:Lnet/minecraft/item/ItemGroup;"))
    private ItemGroup modifySetSelectedTab2() {
        return SEARCH;
    }

    @Redirect(method = "setSelectedTab", at = @At(
            value = "INVOKE",
            target =
                    //? if <=1.15.2 {
                    /*"Lnet/minecraft/item/ItemGroup;appendStacks(Lnet/minecraft/util/collection/DefaultedList;)V"*/
                    //?} else {
                    "Lnet/minecraft/item/ItemGroup;appendStacks(Lnet/minecraft/util/collection/DefaultedList;)V"
                    //?}
    ))
    private void modifySetSelectedTab3(ItemGroup instance, DefaultedList<ItemStack> stacks) {
        this.getNewItemGroup(instance).appendStacks(stacks);
    }

    @Redirect(method = "setSelectedTab", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;INVENTORY:Lnet/minecraft/item/ItemGroup;"))
    private ItemGroup modifySetSelectedTab4() {
        return INVENTORY2;
    }

    @ModifyConstant(method = "setSelectedTab", constant = @Constant(intValue = 6))
    private int modifySetSelectedTab5(int constant) {
        return 7;
    }

    @Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    private int modifySetSelectedTab6(ItemGroup instance) {
        return instance.getIcon().getCount() - 1;
    }

    @Redirect(method = "isClickOutsideBounds", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;GROUPS:[Lnet/minecraft/item/ItemGroup;"))
    protected ItemGroup[] modifyIsClickInTab() {
        return GROUPS;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;GROUPS:[Lnet/minecraft/item/ItemGroup;"))
    public ItemGroup[] modifyRender1() {
        return GROUPS;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    public int modifyRender2(ItemGroup instance) {
        return NewItemGroups.INVENTORY.getIndex();
    }

    @Redirect(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    private int modifyRenderTooltip1(ItemGroup instance) {
        if (selectedTab == NewItemGroups.SEARCH.getIndex() || selectedTab == NewItemGroups.INVENTORY.getIndex() || (this.focusedSlot != null && this.focusedSlot.id >= 45)) {
            return selectedTab;
        }
        return -1;
    }

    @Redirect(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getGroup()Lnet/minecraft/item/ItemGroup;"))
    private ItemGroup modifyRenderTooltip2(Item instance) {
        return null;
    }

    @Redirect(method = "renderTooltip", at = @At(
            value =
                    //? if <=1.16.5 {
                    /*"FIELD",*/
                    //?} else {
                    "INVOKE",
                    //?}
            target =
                    //? if <=1.16.5 {
                    /*"Lnet/minecraft/item/Items;ENCHANTED_BOOK:Lnet/minecraft/item/Item;"*/
                    //?} else {
                    "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
                    //?}
    ))
    private
    //? if <=1.16.5 {
    /*Item*/
    //?} else {
    boolean
    //?}
    modifyRenderTooltip3(
            //? if <=1.16.5 {
            /**/
            //?} else {
            ItemStack instance,
            Item item
            //?}
    ) {
        //? if <=1.16.5 {
        /*return ItemStacks.EMPTY.getItem();*/
        //?} else {
        return false;
        //?}
    }

    @Inject(method = "renderTooltip", at = @At(
            value = "INVOKE",
            target =
                    //? if <=1.15.2 {
                    /*"Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTooltip(Ljava/util/List;II)V"*/
                    //?} else {
                    "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V"
                    //?}
    ))
    private void modifyRenderTooltip4(
            MatrixStack matrices,
            ItemStack stack,
            int x,
            int y,
            CallbackInfo ci,
            @Local(ordinal = 1) LocalRef<
                    List<
                            //? if <=1.15.2 {
                            /*String*/
                            //?} else {
                            Text
                            //?}
                            >
                    > localRef
    ) {
        List<
                //? if <=1.15.2 {
                /*String*/
                //?} else {
                Text
                //?}
                > newItemGroup = this.getNewItemGroups(stack);
        List<
                //? if <=1.15.2 {
                /*String*/
                //?} else {
                Text
                //?}
                > list2 = localRef.get();
        for (
                //? if <=1.15.2 {
                /*String*/
                //?} else {
                Text
                //?}
                        text : newItemGroup) {
            //? if <=1.14.2 {
            /*list2.add(1, "" + ChatFormat.BOLD + ChatFormat.BLUE + text);*/
            //?} elif <=1.15.2 {
            /*list2.add(1, "" + Formatting.BOLD + Formatting.BLUE + text);*/
            //?} else {
            list2.add(1, text
                    //?} if <=1.18.2 {
                    /*.shallowCopy()*/
                    //?} else {
                    .copy()
                    //?}
                    .formatted(Formatting.BLUE));
            //?}
        }
        localRef.set(list2);
    }

    @Unique
    private List<
            //? if <=1.15.2 {
            /*String*/
            //?} else {
            Text
            //?}
            > getNewItemGroups(ItemStack itemStack) {
        List<Text> groups = new ArrayList<>();
        if (
                //? if <=1.16.5 {
                /*itemStack.getItem() == Items.ENCHANTED_BOOK*/
                //?} else {
                itemStack.isOf(Items.ENCHANTED_BOOK)
                //?}
        ) {
            groups.add(
                    //? if <=1.15.2 {
                    /*I18n.translate(NewItemGroups.INGREDIENTS.getDisplayName().getString())*/
                    //?} else {
                    NewItemGroups.INGREDIENTS.getDisplayName()
                    //?}
            );
            return groups;
        }

        for (int i = NewItemGroups.GROUPS.length - 1; 0 <= i; i--) {
            NewItemGroup itemGroup = NewItemGroups.GROUPS[i];
            if (itemGroup.containsItemStack(itemGroup.getItems(), itemStack)) {
                groups.add(
                        //? if <=1.15.2 {
                        /*I18n.translate(itemGroup.getDisplayName().getString())*/
                        //?} else {
                        itemGroup.getDisplayName()
                        //?}
                );
            }
        }
        return groups;
    }

    @Inject(method = "drawBackground", at = @At(value = "HEAD"), cancellable = true)
    private void modifyDrawBackground(
            //? if <=1.15.2 {
            /**/
            //?} else {
            MatrixStack matrices,
            //?}
            float delta, int mouseX, int mouseY, CallbackInfo ci) {
        ci.cancel();
        //? if <=1.14.2 {
        /*GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);*/
        /*GuiLighting.enableForItems();*/
        //?} elif <=1.14.4 {
        /*GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);*/
        /*DiffuseLighting.enableForItems();*/
        //?} elif <=1.16.5 {
        /*RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);*/
        //?} else {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //?}
        ItemGroup itemGroup = GROUPS[selectedTab];
        NewItemGroup itemGroup2 = NewItemGroups.GROUPS[selectedTab];

        int k;
        for(ItemGroup itemGroup3 : GROUPS) {
            //? if <=1.16.5 {
            /*this.minecraft.getTextureManager().bindTexture(TEXTURE);*/
            //?} else {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            //?}

            if (itemGroup3.getIcon().getCount() - 1 != selectedTab) {
                //? if <=1.14.4 {
                /*this.method_2468(itemGroup3);*/
                //?} else {
                this.renderTabIcon(
                        //? if <=1.15.2 {
                        matrices,
                        //?}
                        itemGroup3);
                //?}
            }
        }


        //? if <=1.16.5 {
        /*this.minecraft.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup2.getTexture()));*/
        //?} else {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup2.getTexture()));
        //?}
        //? if <=1.15.2 {
        /*this.blit(this.x, this.y, 0, 0, this.containerWidth, this.containerHeight);*/
        //?} else {
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        //?}
        this.searchBox.render(
                //? if >1.15.2 {
                matrices,
                //?}
                mouseX, mouseY, delta);
        //? if <=1.14.4 {
        /*GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);*/
        //?} elif <=1.16.5 {
        /*RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);*/
        //?} else {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //?}
        int i =
                //? if <=1.14.2 {
                /*this.left + */
                //?} else {
                this.x +
                //?}
                        175;
        int y =
                //? if <=1.14.2 {
                /*this.top + */
                //?} else {
                this.y +
                //?}
                        18;
        k = y + 112;

        //? if <=1.16.5 {
        /*this.minecraft.getTextureManager().bindTexture(TEXTURE);*/
        //?} else {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        //?}
        if (itemGroup != INVENTORY2) {
            //? if <=1.14.2 {
            /*this.blit(i, y + (int)((float)(k - y - 17) * this.scrollPosition), 232 + (this.doRenderScrollBar() ? 0 : 12), 0, 12, 15);*/
            //?} elif <=1.15.2 {
            /*this.blit(i, y + (int)((float)(k - y - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);*/
            //?} else {
            this.drawTexture(matrices, i, y + (int)((float)(k - y - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
            //?}
        }

        //? if <=1.14.4 {
        /*this.method_2468(itemGroup3);*/
        //?} else {
        this.renderTabIcon(
                //? if <=1.15.2 {
                matrices,
                //?}
                itemGroup);
        //?}
        if (itemGroup == INVENTORY2) {
            InventoryScreen.drawEntity(
                    //? if <=1.14.2 {
                    /*this.left + */
                    //?} else {
                    this.x +
                    //?}
                            88,
                    //? if <=1.14.2 {
                    /*this.top + */
                    //?} else {
                    this.y +
                    //?}
                            45,
                    20,
                    (float)(
                            //? if <=1.14.2 {
                            /*this.left + */
                            //?} else {
                            this.x +
                            //?}
                                    88 - mouseX),
                    (float)(
                            //? if <=1.14.2 {
                            /*this.top + */
                            //?} else {
                            this.y +
                            //?}
                                    45 - 30 - mouseY),
                    //? if <=1.15.2 {
                    /*this.minecraft.player*/
                    //?} else {
                    this.client.player
                    //?}
            );
        }
    }

    @Override
    public void drawBackground(
            //? if <=1.15.2 {
            /**/
            //?} else {
            MatrixStack matrices,
            //?}
            float delta, int mouseX, int mouseY) {
    }

    @ModifyConstant(method = "isClickInTab", constant = @Constant(intValue = 28))
    private int modifyIsClickInTab1(int constant) {
        return TAB_WIDTH;
    }

    @ModifyConstant(method = "isClickInTab", constant = @Constant(intValue = 6))
    private int modifyIsClickInTab2(int constant) {
        return 7;
    }

    @Redirect(method = "isClickInTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getColumn()I"))
    private int modifyIsClickInTab3(ItemGroup instance) {
        return this.getNewItemGroup(instance).getColumn();
    }

    @Redirect(method = "isClickInTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isSpecial()Z"))
    private boolean modifyIsClickInTab4(ItemGroup instance) {
        return this.getNewItemGroup(instance).isSpecial();
    }

    @Redirect(method = "isClickInTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isTopRow()Z"))
    private boolean modifyIsClickInTab5(ItemGroup instance) {
        return this.getNewItemGroup(instance).isTopRow();
    }

    @ModifyConstant(
            method =
                    //? if <=1.14.4 {
                    /*method_2471*/
                    //?} else {
                    "renderTabTooltipIfHovered",
                    //?}
            constant = @Constant(intValue = 28)
    )
    private int modifyRenderTabTooltipIfHovered1(int constant) {
        return TAB_WIDTH;
    }

    @ModifyConstant(
            method =
                    //? if <=1.14.4 {
                    /*method_2471*/
                    //?} else {
                    "renderTabTooltipIfHovered",
                    //?}
            constant = @Constant(intValue = 6)
    )
    private int modifyRenderTabTooltipIfHovered2(int constant) {
        return 7;
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2471*/
                    //?} else {
                    "renderTabTooltipIfHovered",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getColumn()I")
    )
    private int modifyRenderTabTooltipIfHovered3(ItemGroup instance) {
        return this.getNewItemGroup(instance).getColumn();
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2471*/
                    //?} else {
                    "renderTabTooltipIfHovered",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isSpecial()Z")
    )
    private boolean modifyRenderTabTooltipIfHovered4(ItemGroup instance) {
        return this.getNewItemGroup(instance).isSpecial();
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2471*/
                    //?} else {
                    "renderTabTooltipIfHovered",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isTopRow()Z")
    )
    private boolean modifyRenderTabTooltipIfHovered5(ItemGroup instance) {
        return this.getNewItemGroup(instance).isTopRow();
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2471*/
                    //?} else {
                    "renderTabTooltipIfHovered",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getDisplayName()Lnet/minecraft/text/Text;")
    )
    private Text modifyRenderTabTooltipIfHovered6(ItemGroup instance) {
        return this.getNewItemGroup(instance).getDisplayName();
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2468*/
                    //?} else {
                    "renderTabIcon",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I")
    )
    private int modifyRenderTabIcon1(ItemGroup instance) {
        return instance.getIcon().getCount() - 1;
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2468*/
                    //?} else {
                    "renderTabIcon",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isTopRow()Z")
    )
    private boolean modifyRenderTabIcon2(ItemGroup instance) {
        return this.getNewItemGroup(instance).isTopRow();
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2468*/
                    //?} else {
                    "renderTabIcon",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getColumn()I")
    )
    private int modifyRenderTabIcon3(ItemGroup instance) {
        return instance.getIcon().getCount() - 1;
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2468*/
                    //?} else {
                    "renderTabIcon",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isSpecial()Z")
    )
    private boolean modifyRenderTabIcon4(ItemGroup instance) {
        return false;
    }

    @ModifyVariable(
            method =
                    //? if <=1.14.4 {
                    /*method_2468*/
                    //?} else {
                    "renderTabIcon",
                    //?}
            at = @At("STORE"),
            ordinal = 3
    )
    private int modifyRenderTabIcon5(int x) {
        return 0;
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2468*/
                    //?} else {
                    "renderTabIcon",
                    //?}
            at = @At(
                    value = "INVOKE",
                    target =
                            //? if <=1.15.2 {
                            /*"Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;blit(IIIIII)V"*/
                            //?} else {
                            "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
                            //?}
            )
    )
    private void modifyRenderTabIcon6(CreativeInventoryScreen instance,
                                      //? if >1.15.2 {
                                      MatrixStack matrixStack,
                                      //?}
                                      int x, int y, int u, int v, int width, int height) {
        int index = u / 28;
        NewItemGroup group = NewItemGroups.GROUPS[index];
        x =
                //? if <=1.14.2 {
                /*this.left + */
                //?} else {
                this.x +
                //?}
                        this.getTabX(group);
        int j = (u / 28) % 7;
        this.renderTab(matrixStack, x, y, j == 6 ? 5 * 28 : j * 28, v);
    }

    @ModifyArg(
            method =
                    //? if <=1.14.4 {
                    /*method_2468*/
                    //?} else {
                    "renderTabIcon",
                    //?}
            at = @At(
                    value = "INVOKE",
                    target =
                            //? if <=1.15.2 {
                            /*"Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItem(Lnet/minecraft/item/ItemStack;II)V"*/
                            //?} else {
                            "Lnet/minecraft/client/render/item/ItemRenderer;renderInGuiWithOverrides(Lnet/minecraft/item/ItemStack;II)V"
                            //?}
            ),
            index = 1
    )
    private int modifyRenderTabIcon7(int x, @Local(ordinal = 1) int u) {
        NewItemGroup group = NewItemGroups.GROUPS[u / 28];
        return
                //? if <=1.14.2 {
                /*this.left + */
                //?} else {
                this.x +
                //?}
                        this.getTabX(group) + 5;
    }

    @ModifyArg(
            method =
                    //? if <=1.14.4 {
                    /*method_2468*/
                    //?} else {
                    "renderTabIcon",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"),
            index = 2
    )
    private int modifyRenderTabIcon8(int x, @Local(ordinal = 1) int u) {
        NewItemGroup group = NewItemGroups.GROUPS[u / 28];
        return this.x + this.getTabX(group) + 5;
    }

    @Redirect(
            method =
                    //? if <=1.14.4 {
                    /*method_2468*/
                    //?} else {
                    "renderTabIcon",
                    //?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIcon()Lnet/minecraft/item/ItemStack;")
    )
    private ItemStack modifyRenderTabIcon9(ItemGroup instance) {
        ItemStack stack = instance.getIcon().copy();
        stack.setCount(1);
        return stack;
    }

    @Unique
    private NewItemGroup getNewItemGroup(ItemGroup itemGroup) {
        return NewItemGroups.GROUPS[itemGroup.getIcon().getCount() - 1];
    }

    @Unique
    private int getTabX(NewItemGroup group) {
        int i = group.getColumn();
        int k = (TAB_WIDTH + 1) * i;
        if (group.isSpecial()) {
            k = this.backgroundWidth - (TAB_WIDTH + 1) * (7 - i) + 1;
        }
        return k;
    }

    @Unique
    private void renderTab(MatrixStack matrices, int x, int y, int u, int v) {
        this.drawTexture(matrices, x, y, u, v, 5, TAB_HEIGHT);
        this.drawTexture(matrices, x + 5, y, u + 7, v, TAB_WIDTH - 5, TAB_HEIGHT);
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    private static int modifySelectedTab(ItemGroup instance) {
        return NewItemGroups.BUILDING_BLOCKS.getIndex();
    }

    static {
        BUILDING_BLOCKS = new ItemGroup(0, "building_blocks") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Blocks.BRICKS);
                stack.setCount(1);
                return stack;
            }
        };
        COLORED_BLOCKS = new ItemGroup(0, "colored_blocks") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Blocks.CYAN_WOOL);
                stack.setCount(2);
                return stack;
            }
        };
        NATURAL = new ItemGroup(0, "natural_blocks") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Blocks.GRASS_BLOCK);
                stack.setCount(3);
                return stack;
            }
        };
        FUNCTIONAL = new ItemGroup(0, "functional_blocks") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Items.OAK_SIGN);
                stack.setCount(4);
                return stack;
            }
        };
        REDSTONE = new ItemGroup(0, "redstone_blocks") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Items.REDSTONE);
                stack.setCount(5);
                return stack;
            }
        };
        HOTBAR = new ItemGroup(0, "hotbar") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Blocks.BOOKSHELF);
                stack.setCount(6);
                return stack;
            }
        };
        SEARCH = new ItemGroup(0, "search") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Items.COMPASS);
                stack.setCount(7);
                return stack;
            }
        };
        TOOLS = new ItemGroup(0, "tools_and_utilities") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
                stack.setCount(8);
                return stack;
            }
        };
        COMBAT = new ItemGroup(0, "combat") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(
                        //? if <1.16 {
                        /*Items.DIAMOND_SWORD*/
                        //?} else {
                        Items.NETHERITE_SWORD
                        //?}
                );
                stack.setCount(9);
                return stack;
            }
        };
        FOOD_AND_DRINK = new ItemGroup(0, "food_and_drinks") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Items.GOLDEN_APPLE);
                stack.setCount(10);
                return stack;
            }
        };
        INGREDIENTS = new ItemGroup(0, "ingredients") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Items.IRON_INGOT);
                stack.setCount(11);
                return stack;
            }
        };
        SPAWN_EGGS = new ItemGroup(0, "spawn_eggs") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Items.PIG_SPAWN_EGG);
                stack.setCount(12);
                return stack;
            }
        };
        OPERATOR = new ItemGroup(0, "op_blocks") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Items.COMMAND_BLOCK);
                stack.setCount(13);
                return stack;
            }
        };
        INVENTORY2 = new ItemGroup(0, "inventory") {
            @Override
            public ItemStack createIcon() {
                ItemStack stack = new ItemStack(Blocks.CHEST);
                stack.setCount(14);
                return stack;
            }
        };
        GROUPS = new ItemGroup[]{BUILDING_BLOCKS, COLORED_BLOCKS, NATURAL, FUNCTIONAL, REDSTONE, HOTBAR, SEARCH, TOOLS, COMBAT, FOOD_AND_DRINK, INGREDIENTS, SPAWN_EGGS, OPERATOR, INVENTORY2};
    }
}
