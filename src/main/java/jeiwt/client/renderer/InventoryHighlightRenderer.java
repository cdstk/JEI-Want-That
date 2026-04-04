package jeiwt.client.renderer;

import jeiwt.JEIWantThat;
import jeiwt.client.handlers.KeyHandler;
import jeiwt.handlers.ForgeConfigHandler;
import jeiwt.util.JEIUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryHighlightRenderer {

    // Background Render in jeiwt.mixin.vanilla.GuiContainer_InventoryHighlightMixin

    @SubscribeEvent
    public static void renderForeground(GuiContainerEvent.DrawForeground event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null || mc.currentScreen == null) return;
        if (!KeyHandler.renderDisplay()) return;
        JEIWantThat.setSkipModdedTooltips();

        GuiContainer guiContainer = event.getGuiContainer();
        List<Slot> backRender = new ArrayList<>();
        List<Slot> frontRender = new ArrayList<>();

        guiContainer.inventorySlots.inventorySlots.forEach(slot -> {
            if(JEIUtil.isItemStackDesirable(slot.getStack())){
                frontRender.add(slot);
            }
            else {
                backRender.add(slot);
            }
        });
        if(ForgeConfigHandler.client.darkenNonDesirable) {
            backRender.forEach(InventoryHighlightRenderer::drawDarkenSlot);
        }

        backRender.sort((left, right) -> right.yPos - left.yPos);
        if(KeyHandler.renderModifiedTooltip() || KeyHandler.renderFullTooltip()) {
            frontRender.forEach(slot -> {
                boolean renderSlot = true;
                switch (ForgeConfigHandler.client.inventoryShiftRender){
                    case VERTICAL:
                        renderSlot = guiContainer.getSlotUnderMouse() != null && guiContainer.getSlotUnderMouse().xPos == slot.xPos;
                        break;
                    case HORIZONTAL:
                        renderSlot = guiContainer.getSlotUnderMouse() != null && guiContainer.getSlotUnderMouse().yPos == slot.yPos;
                        break;
                }
                if(renderSlot){
                    GuiUtils.drawHoveringText(
                            slot.getStack(),
                            JEIUtil.getDesirableTooltip(slot.getStack()),
                            slot.xPos + ForgeConfigHandler.client.xInventoryOffset,
                            slot.yPos - ForgeConfigHandler.client.yInventoryOffset + 16,
                            mc.displayWidth,
                            mc.displayHeight,
                            -1,
                            mc.fontRenderer
                    );
                }
            });
        }
        JEIWantThat.resetSkipModdedTooltips();
    }

    public static void drawDarkenSlot(Slot slot) {
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.colorMask(true, true, true, false);
        GuiUtils.drawGradientRect(
                0,
                slot.xPos,
                slot.yPos,
                slot.xPos + 16,
                slot.yPos + 16,
                0x80000000,
                0x80000000
        );
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
    }

    public static void drawBorderSlot(Slot slot, ItemStack itemStack) {
        Minecraft mc = Minecraft.getMinecraft();
        GuiUtils.drawHoveringText(
                itemStack,
                Collections.singletonList("  "),
                slot.xPos - 8,
                slot.yPos + 16,
                mc.displayWidth,
                mc.displayHeight,
                -1,
                mc.fontRenderer
        );
    }
}
