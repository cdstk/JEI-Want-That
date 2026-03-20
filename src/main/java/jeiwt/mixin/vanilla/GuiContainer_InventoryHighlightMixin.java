package jeiwt.mixin.vanilla;

import jeiwt.JEIWantThat;
import jeiwt.client.handlers.KeyHandler;
import jeiwt.client.renderer.InventoryHighlightRenderer;
import jeiwt.handlers.ForgeConfigHandler;
import jeiwt.util.JEIUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class GuiContainer_InventoryHighlightMixin {

    @Inject(
            method = "drawSlot",
            at = @At("HEAD")
    )
    private void jeiwt_vanillaGuiContainer_drawSlotBackGround(Slot slot, CallbackInfo ci){
        if (!ForgeConfigHandler.client.backgroundForDesirable) return;
        if (!KeyHandler.isKeyDown(KeyHandler.enableDisplay)) return;

        if(JEIUtil.isItemStackDesirable(slot.getStack())){
            JEIWantThat.setSkipModdedTooltips();
            InventoryHighlightRenderer.drawBorderSlot(slot, slot.getStack());
            RenderHelper.enableGUIStandardItemLighting();
            JEIWantThat.resetSkipModdedTooltips();
        }
    }
}
