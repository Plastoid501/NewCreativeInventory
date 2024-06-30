package net.plastoid501.nci.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
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
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Shadow private static int selectedTab;
    @Shadow private float scrollPosition;
    @Shadow private TextFieldWidget searchBox;
    @Shadow public abstract boolean hasScrollbar();
    @Shadow protected abstract void renderTabIcon(MatrixStack matrices, ItemGroup group);

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final int TAB_WIDTH = 26;
    private static final int TAB_HEIGHT = 32;
    private static final ItemGroup[] GROUPS;
    private static final ItemGroup BUILDING_BLOCKS;
    private static final ItemGroup COLORED_BLOCKS;
    private static final ItemGroup NATURAL;
    private static final ItemGroup FUNCTIONAL;
    private static final ItemGroup REDSTONE;
    private static final ItemGroup HOTBAR;
    private static final ItemGroup SEARCH;
    private static final ItemGroup TOOLS;
    private static final ItemGroup COMBAT;
    private static final ItemGroup FOOD_AND_DRINK;
    private static final ItemGroup INGREDIENTS;
    private static final ItemGroup SPAWN_EGGS;
    private static final ItemGroup OPERATOR;
    private static final ItemGroup INVENTORY2;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Redirect(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    protected int modifyOnMouseClick1(ItemGroup instance) {
        return NewItemGroups.INVENTORY.getIndex();
    }

    @Inject(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;getStacks()Lnet/minecraft/util/collection/DefaultedList;"))
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

    @Redirect(method = "search", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
    private boolean modifySearch(Iterator<Item> instance) {
        for (NewItemGroup itemGroup : NewItemGroups.GROUPS) {
            for (ItemStack itemStack : itemGroup.getItems()) {
                itemGroup.appendStacksWithoutSameItemStack(this.handler.itemList, itemStack);
            }
        }
        return false;
    }

    @Redirect(method = "drawForeground", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;GROUPS:[Lnet/minecraft/item/ItemGroup;"))
    protected ItemGroup[] modifyDrawForeground1() {
        return GROUPS;
    }

    @Redirect(method = "drawForeground", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;shouldRenderName()Z"))
    protected boolean modifyDrawForeground2(ItemGroup instance) {
        return instance != INVENTORY2;
    }

    @Redirect(method = "drawForeground", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getDisplayName()Lnet/minecraft/text/Text;"))
    protected Text modifyDrawForeground3(ItemGroup instance) {
        return this.getNewItemGroup(instance).getDisplayName();
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
        cir.setReturnValue(selectedTab != NewItemGroups.INVENTORY.getIndex() && this.handler.shouldShowScrollbar());
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

    @Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;appendStacks(Lnet/minecraft/util/collection/DefaultedList;)V"))
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

    @Redirect(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean modifyRenderTooltip3(ItemStack instance, Item item) {
        return false;
    }

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void modifyRenderTooltip4(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo ci, @Local(ordinal = 1) LocalRef<List<Text>> localRef) {
        List<Text> newItemGroup = this.getNewItemGroup(stack);
        List<Text> list2 = localRef.get();
        for (Text text : newItemGroup) {
            list2.add(1, text.copy().formatted(Formatting.BLUE));
        }
        localRef.set(list2);
    }

    @Unique
    private List<Text> getNewItemGroup(ItemStack itemStack) {
        List<Text> groups = new ArrayList<>();
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            groups.add(NewItemGroups.INGREDIENTS.getDisplayName());
            return groups;
        }

        for (int i = NewItemGroups.GROUPS.length - 1; 0 <= i; i--) {
            NewItemGroup itemGroup = NewItemGroups.GROUPS[i];
            if (itemGroup.containsItemStack(itemGroup.getItems(), itemStack)) {
                groups.add(itemGroup.getDisplayName());
            }
        }
        return groups;
    }

    @Inject(method = "drawBackground", at = @At(value = "HEAD"), cancellable = true)
    private void modifyDrawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        ci.cancel();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        ItemGroup itemGroup = GROUPS[selectedTab];
        NewItemGroup itemGroup2 = NewItemGroups.GROUPS[selectedTab];

        int k;
        for(ItemGroup itemGroup3 : GROUPS) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            if (itemGroup3.getIcon().getCount() - 1 != selectedTab) {
                this.renderTabIcon(matrices, itemGroup3);
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup2.getTexture()));
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.x + 175;
        int y = this.y + 18;
        k = y + 112;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        if (itemGroup != INVENTORY2) {
            this.drawTexture(matrices, i, y + (int)((float)(k - y - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
        }

        this.renderTabIcon(matrices, itemGroup);
        if (itemGroup == INVENTORY2) {
            InventoryScreen.drawEntity(this.x + 88, this.y + 45, 20, (float)(this.x + 88 - mouseX), (float)(this.y + 45 - 30 - mouseY), this.client.player);
        }
    }

    @Override
    public void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
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

    @ModifyConstant(method = "renderTabTooltipIfHovered", constant = @Constant(intValue = 28))
    private int modifyRenderTabTooltipIfHovered1(int constant) {
        return TAB_WIDTH;
    }

    @ModifyConstant(method = "renderTabTooltipIfHovered", constant = @Constant(intValue = 6))
    private int modifyRenderTabTooltipIfHovered2(int constant) {
        return 7;
    }

    @Redirect(method = "renderTabTooltipIfHovered", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getColumn()I"))
    private int modifyRenderTabTooltipIfHovered3(ItemGroup instance) {
        return this.getNewItemGroup(instance).getColumn();
    }

    @Redirect(method = "renderTabTooltipIfHovered", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isSpecial()Z"))
    private boolean modifyRenderTabTooltipIfHovered4(ItemGroup instance) {
        return this.getNewItemGroup(instance).isSpecial();
    }

    @Redirect(method = "renderTabTooltipIfHovered", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isTopRow()Z"))
    private boolean modifyRenderTabTooltipIfHovered5(ItemGroup instance) {
        return this.getNewItemGroup(instance).isTopRow();
    }

    @Redirect(method = "renderTabTooltipIfHovered", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getDisplayName()Lnet/minecraft/text/Text;"))
    private Text modifyRenderTabTooltipIfHovered6(ItemGroup instance) {
        return this.getNewItemGroup(instance).getDisplayName();
    }

    @Redirect(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    private int modifyRenderTabIcon1(ItemGroup instance) {
        return instance.getIcon().getCount() - 1;
    }

    @Redirect(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isTopRow()Z"))
    private boolean modifyRenderTabIcon2(ItemGroup instance) {
        return this.getNewItemGroup(instance).isTopRow();
    }

    @Redirect(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getColumn()I"))
    private int modifyRenderTabIcon3(ItemGroup instance) {
        return instance.getIcon().getCount() - 1;
    }

    @Redirect(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;isSpecial()Z"))
    private boolean modifyRenderTabIcon4(ItemGroup instance) {
        return false;
    }

    @ModifyVariable(method = "renderTabIcon", at = @At("STORE"), ordinal = 3)
    private int modifyRenderTabIcon5(int x) {
        return 0;
    }

    @Redirect(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    private void modifyRenderTabIcon6(CreativeInventoryScreen instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height) {
        int index = u / 28;
        NewItemGroup group = NewItemGroups.GROUPS[index];
        x = this.x + this.getTabX(group);
        int j = (u / 28) % 7;
        this.renderTab(matrixStack, x, y, j == 6 ? 5 * 28 : j * 28, v);
    }

    @ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderInGuiWithOverrides(Lnet/minecraft/item/ItemStack;II)V"), index = 1)
    private int modifyRenderTabIcon7(int x, @Local(ordinal = 1) int u) {
        NewItemGroup group = NewItemGroups.GROUPS[u / 28];
        return this.x + this.getTabX(group) + 5;
    }

    @ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"), index = 2)
    private int modifyRenderTabIcon8(int x, @Local(ordinal = 1) int u) {
        NewItemGroup group = NewItemGroups.GROUPS[u / 28];
        return this.x + this.getTabX(group) + 5;
    }

    @Redirect(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIcon()Lnet/minecraft/item/ItemStack;"))
    private ItemStack modifyRenderTabIcon9(ItemGroup instance) {
        ItemStack stack = instance.getIcon().copy();
        stack.setCount(1);
        return stack;
    }

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
                ItemStack stack = new ItemStack(Items.NETHERITE_SWORD);
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
