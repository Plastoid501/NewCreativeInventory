package net.plastoid501.nci.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryListener;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.plastoid501.nci.item.NewItemGroup;
import net.plastoid501.nci.item.NewItemGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Shadow private static int selectedTab;
    @Shadow private List<Slot> slots;
    @Shadow private Slot deleteItemSlot;
    @Shadow static final SimpleInventory INVENTORY = new SimpleInventory(45);
    @Shadow private TextFieldWidget searchBox;
    @Shadow public float scrollPosition;
    @Shadow private boolean scrolling;
    @Shadow private boolean lastClickOutsideBounds;
    @Shadow private CreativeInventoryListener listener;
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final int ROWS_COUNT = 5;
    private static final int COLUMNS_COUNT = 9;
    private static final int TAB_WIDTH = 26;
    private static final int TAB_HEIGHT = 32;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;


    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Redirect(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    protected int modifyGetIndex1(ItemGroup instance) {
        return NewItemGroups.INVENTORY.getIndex();
    }

    @Redirect(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    private int modifyGetIndex2(ItemGroup instance) {
        return NewItemGroups.SEARCH.getIndex();
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    private int modifyGetIndex3(ItemGroup instance) {
        return NewItemGroups.SEARCH.getIndex();
    }

    @Inject(method = "init()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;addSelectableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"), cancellable = true)
    protected void modifySetSelectedTab1(CallbackInfo ci) {
        if (this.client.currentScreen instanceof CreativeInventoryScreen screen) {
            int i = selectedTab;
            selectedTab = -1;
            this.setSelectedTab(screen, NewItemGroups.GROUPS[i]);
            this.client.player.playerScreenHandler.removeListener(this.listener);
            this.listener = new CreativeInventoryListener(this.client);
            this.client.player.playerScreenHandler.addListener(this.listener);
        }
        ci.cancel();
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;setSelectedTab(Lnet/minecraft/item/ItemGroup;)V"))
    private void modifySetSelectedTab2(CreativeInventoryScreen instance, ItemGroup group) {
        this.setSelectedTab(instance, NewItemGroups.SEARCH);
    }

    @Inject(method = "isClickOutsideBounds", at = @At(value = "HEAD"), cancellable = true)
    protected void modifyIsClickInTab(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        this.lastClickOutsideBounds = bl && !this.isClickInTab(NewItemGroups.GROUPS[selectedTab], mouseX, mouseY);
        cir.setReturnValue(this.lastClickOutsideBounds);
        cir.cancel();
    }

    private void setSelectedTab(CreativeInventoryScreen screen, NewItemGroup group) {
        int i = selectedTab;
        selectedTab = group.getIndex();
        this.cursorDragSlots.clear();
        this.handler.itemList.clear();
        int invSlot;
        int y;
        if (group == NewItemGroups.HOTBAR) {
            HotbarStorage hotbarStorage = this.client.getCreativeHotbarStorage();

            for(invSlot = 0; invSlot < COLUMNS_COUNT; ++invSlot) {
                HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(invSlot);
                if (hotbarStorageEntry.isEmpty()) {
                    for(y = 0; y < COLUMNS_COUNT; ++y) {
                        if (y == invSlot) {
                            ItemStack itemStack = new ItemStack(Items.PAPER);
                            itemStack.getOrCreateSubNbt("CustomCreativeLock");
                            Text text = this.client.options.hotbarKeys[invSlot].getBoundKeyLocalizedText();
                            Text text2 = this.client.options.saveToolbarActivatorKey.getBoundKeyLocalizedText();
                            itemStack.setCustomName(new TranslatableText("inventory.hotbarInfo", text2, text));
                            this.handler.itemList.add(itemStack);
                        } else {
                            this.handler.itemList.add(ItemStack.EMPTY);
                        }
                    }
                } else {
                    this.handler.itemList.addAll(hotbarStorageEntry);
                }
            }
        } else if (group != NewItemGroups.SEARCH) {
            group.appendStacks(this.handler.itemList);
        }

        if (group == NewItemGroups.INVENTORY) {
            ScreenHandler screenHandler = this.client.player.playerScreenHandler;
            if (this.slots == null) {
                this.slots = ImmutableList.copyOf(this.handler.slots);
            }

            this.handler.slots.clear();

            for(invSlot = 0; invSlot < screenHandler.slots.size(); ++invSlot) {
                int x;
                int l;
                int m;
                int n;
                if (invSlot >= ROWS_COUNT && invSlot < COLUMNS_COUNT) {
                    l = invSlot - ROWS_COUNT;
                    m = l / 2;
                    n = l % 2;
                    x = 54 + m * 54;
                    y = 7 + n * 27;
                } else if (invSlot >= 0 && invSlot < ROWS_COUNT) {
                    x = -2000;
                    y = -2000;
                } else if (invSlot == 45) {
                    x = 35;
                    y = 20;
                } else {
                    l = invSlot - COLUMNS_COUNT;
                    m = l % COLUMNS_COUNT;
                    n = l / COLUMNS_COUNT;
                    x = COLUMNS_COUNT + m * 18;
                    if (invSlot >= 36) {
                        y = 112;
                    } else {
                        y = 54 + n * 18;
                    }
                }

                Slot slot = new CreativeInventoryScreen.CreativeSlot(screenHandler.slots.get(invSlot), invSlot, x, y);
                this.handler.slots.add(slot);
            }

            this.deleteItemSlot = new Slot(INVENTORY, 0, 173, 112);
            this.handler.slots.add(this.deleteItemSlot);
        } else if (i == NewItemGroups.INVENTORY.getIndex()) {
            this.handler.slots.clear();
            this.handler.slots.addAll(this.slots);
            this.slots = null;
        }

        if (this.searchBox != null) {
            if (group == NewItemGroups.SEARCH) {
                this.searchBox.setVisible(true);
                this.searchBox.setFocusUnlocked(false);
                this.searchBox.setTextFieldFocused(true);
                if (i != group.getIndex()) {
                    this.searchBox.setText("");
                }

                screen.search();
            } else {
                this.searchBox.setVisible(false);
                this.searchBox.setFocusUnlocked(true);
                this.searchBox.setTextFieldFocused(false);
                this.searchBox.setText("");
            }
        }

        this.scrollPosition = 0.0F;
        this.handler.scrollItems(0.0F);
    }

    @Redirect(method = "search", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
    private boolean modifyAppendStacks(Iterator<Item> instance) {
        for (NewItemGroup itemGroup : NewItemGroups.GROUPS) {
            for (ItemStack itemStack : itemGroup.getItemGroup()) {
                itemGroup.appendStacksWithoutSameItemStack(this.handler.itemList, itemStack);
            }
        }

        return false;
    }

    @Inject(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;getStacks()Lnet/minecraft/util/collection/DefaultedList;"))
    private void modifyInventoryClear(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (this.client == null || this.client.player == null) {
            return;
        }
        this.client.player.getInventory().clear();

    }

    @Inject(method = "drawBackground", at = @At(value = "HEAD"), cancellable = true)
    private void cancelDrawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.client.currentScreen instanceof CreativeInventoryScreen screen) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            NewItemGroup itemGroup = NewItemGroups.GROUPS[selectedTab];
            NewItemGroup[] itemGroups = NewItemGroups.GROUPS;
            int j = itemGroups.length;

            int k;
            for(k = 0; k < j; ++k) {
                NewItemGroup itemGroup2 = itemGroups[k];
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, TEXTURE);
                if (itemGroup2.getIndex() != selectedTab) {
                    this.renderTabIcon(matrices, itemGroup2);
                }
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup.getTexture()));
            this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
            this.searchBox.render(matrices, mouseX, mouseY, delta);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int i = this.x + 175;
            j = this.y + 18;
            k = j + 112;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            if (itemGroup != NewItemGroups.INVENTORY) {
                this.drawTexture(matrices, i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232 + (screen.hasScrollbar() ? 0 : SCROLLBAR_WIDTH), 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
            }

            this.renderTabIcon(matrices, itemGroup);
            if (itemGroup == NewItemGroups.INVENTORY) {
                InventoryScreen.drawEntity(this.x + 88, this.y + 45, 20, (float)(this.x + 88 - mouseX), (float)(this.y + 45 - 30 - mouseY), this.client.player);
            }
        }

        ci.cancel();
    }

    @Override
    public void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
    }

    private int getTabX(NewItemGroup group) {
        int i = group.getColumn();
        int k = (TAB_WIDTH + 1) * i;
        if (group.isSpecial()) {
            k = this.backgroundWidth - (TAB_WIDTH + 1) * (7 - i) + 1;
        }

        return k;
    }

    private void renderTab(MatrixStack matrices, int x, int y, int u, int v) {
        this.drawTexture(matrices, x, y, u, v, 5, TAB_HEIGHT);
        this.drawTexture(matrices, x + 5, y, u + 7, v, TAB_WIDTH - 5, TAB_HEIGHT);
    }

    protected void renderTabIcon(MatrixStack matrices, NewItemGroup group) {
        boolean bl = group.getIndex() == selectedTab;
        boolean bl2 = group.isTopRow();
        int i = group.getColumn();
        if (i == 6) {
            i = 5;
        }
        int u = i * 28;
        int v = 0;
        int x = this.x + this.getTabX(group);
        int y = this.y;
        if (bl) {
            v += TAB_HEIGHT;
        }

        if (bl2) {
            y -= 28;
        } else {
            v += 64;
            y += this.backgroundHeight - 4;
        }

        this.renderTab(matrices, x, y, u, v);
        this.itemRenderer.zOffset = 100.0F;
        x += 5;
        y += 8 + (bl2 ? 1 : -1);
        ItemStack itemStack = group.getIcon();
        this.itemRenderer.renderInGuiWithOverrides(itemStack, x, y);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, x, y);
        this.itemRenderer.zOffset = 0.0F;
    }

    private List<Text> getNewItemGroup(ItemStack itemStack) {
        List<Text> groups = new ArrayList<>();
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            groups.add(NewItemGroups.INGREDIENTS.getDisplayName());
            return groups;
        }

        for (int i = NewItemGroups.GROUPS.length - 1; 0 <= i; i--) {
            NewItemGroup itemGroup = NewItemGroups.GROUPS[i];
            if (itemGroup.containsItemStack(itemGroup.getItemGroup(), itemStack)) {
                groups.add(itemGroup.getDisplayName());
            }
        }
        return groups;
    }

    @Inject(method = "renderTooltip", at = @At(value = "HEAD"), cancellable = true)
    public void modifyRenderTooltip(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo ci) {
        ci.cancel();
        if (this.client.currentScreen instanceof CreativeInventoryScreen screen) {
            if (selectedTab == NewItemGroups.SEARCH.getIndex() || selectedTab == NewItemGroups.INVENTORY.getIndex() || (this.focusedSlot != null && this.focusedSlot.id >= 45)) {
                List<Text> list = stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
                List<Text> list2 = Lists.newArrayList(list);
                screen.searchResultTags.forEach((tagKey) -> {
                    if (stack.isIn(tagKey)) {
                        list2.add(1, (new LiteralText("#" + tagKey.id())).formatted(Formatting.DARK_PURPLE));
                    }
                });

                List<Text> newItemGroup = this.getNewItemGroup(stack);
                for (Text text : newItemGroup) {
                    list2.add(1, text.shallowCopy().formatted(Formatting.BLUE));
                }

                this.renderTooltip(matrices, list2, stack.getTooltipData(), x, y);
            } else {
                super.renderTooltip(matrices, stack, x, y);
            }
        }
    }

    @Inject(method = "drawForeground", at = @At(value = "HEAD"), cancellable = true)
    protected void modifyDrawForeground(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci) {
        NewItemGroup itemGroup = NewItemGroups.GROUPS[selectedTab];
        if (itemGroup != NewItemGroups.INVENTORY) {
            RenderSystem.disableBlend();
            this.textRenderer.draw(matrices, itemGroup.getDisplayName(), 8.0F, 6.0F, 4210752);
        }
        ci.cancel();
    }

    @Inject(method = "mouseClicked", at = @At(value = "HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if (this.client.currentScreen instanceof CreativeInventoryScreen screen) {
            if (button == 0) {
                double d = mouseX - (double) this.x;
                double e = mouseY - (double) this.y;
                NewItemGroup[] itemGroups = NewItemGroups.GROUPS;

                for (NewItemGroup itemGroup : itemGroups) {
                    if (this.isClickInTab(itemGroup, d, e)) {
                        cir.setReturnValue(true);
                        return;
                    }
                }

                if (selectedTab != NewItemGroups.INVENTORY.getIndex() && screen.isClickInScrollbar(mouseX, mouseY)) {
                    this.scrolling = screen.hasScrollbar();
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        cir.setReturnValue(super.mouseClicked(mouseX, mouseY, button));
    }

    @Inject(method = "hasScrollbar", at = @At("HEAD"), cancellable = true)
    private void modifyHasScrollbar(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(selectedTab != NewItemGroups.INVENTORY.getIndex() && this.handler.shouldShowScrollbar());
        cir.cancel();
    }

    protected boolean isClickInTab(NewItemGroup group, double mouseX, double mouseY) {
        int i = group.getColumn();
        int j = TAB_WIDTH * i;
        int k = 0;
        if (group.isSpecial()) {
            j = this.backgroundWidth - TAB_WIDTH * (7 - i) + 2;
        } else if (i > 0) {
            j += i;
        }

        if (group.isTopRow()) {
            k -= TAB_HEIGHT;
        } else {
            k += this.backgroundHeight;
        }

        return mouseX >= (double)j && mouseX <= (double)(j + TAB_WIDTH) && mouseY >= (double)k && mouseY <= (double)(k + TAB_HEIGHT);
    }

    @Inject(method = "mouseReleased", at = @At(value = "HEAD"), cancellable = true)
    public void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if (this.client.currentScreen instanceof CreativeInventoryScreen screen) {
            if (button == 0) {
                double d = mouseX - (double) this.x;
                double e = mouseY - (double) this.y;
                this.scrolling = false;
                NewItemGroup[] groups = NewItemGroups.GROUPS;

                for (NewItemGroup itemGroup : groups) {
                    if (this.isClickInTab(itemGroup, d, e)) {
                        this.setSelectedTab(screen, itemGroup);
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }

        cir.setReturnValue(super.mouseReleased(mouseX, mouseY, button));
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", shift = At.Shift.AFTER), cancellable = true)
    private void modifyRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        NewItemGroup[] itemGroups = NewItemGroups.GROUPS;

        for (NewItemGroup itemGroup : itemGroups) {
            if (this.renderTabTooltipIfHovered(matrices, itemGroup, mouseX, mouseY)) {
                break;
            }
        }

        if (this.deleteItemSlot != null && selectedTab == NewItemGroups.INVENTORY.getIndex() && this.isPointWithinBounds(this.deleteItemSlot.x, this.deleteItemSlot.y, 16, 16, mouseX, mouseY)) {
            this.renderTooltip(matrices, new TranslatableText("inventory.binSlot"), mouseX, mouseY);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);

        ci.cancel();
    }

    protected boolean renderTabTooltipIfHovered(MatrixStack matrices, NewItemGroup group, int mouseX, int mouseY) {
        int i = group.getColumn();
        int j = TAB_WIDTH * i;
        int k = 0;
        if (group.isSpecial()) {
            j = this.backgroundWidth - TAB_WIDTH * (7 - i) + 2;
        } else if (i > 0) {
            j += i;
        }

        if (group.isTopRow()) {
            k -= TAB_HEIGHT;
        } else {
            k += this.backgroundHeight;
        }

        if (this.isPointWithinBounds(j + 3, k + 3, 23, 27, mouseX, mouseY)) {
            this.renderTooltip(matrices, group.getDisplayName(), mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIndex()I"))
    private static int modifySelectedTab(ItemGroup instance) {
        return NewItemGroups.BUILDING_BLOCKS.getIndex();
    }



}
