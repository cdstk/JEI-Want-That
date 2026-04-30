package jeiwt.client.renderer;

import jeiwt.JEIWantThat;
import jeiwt.client.handlers.KeyHandler;
import jeiwt.handlers.ForgeConfigHandler;
import jeiwt.handlers.ForgeConfigProvider;
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

    private static boolean renderStackBackground = false;
    public static boolean isRenderingStackBackground() {
        return renderStackBackground;
    }

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
        if(ForgeConfigHandler.client.lightenDesirable) {
            frontRender.forEach(InventoryHighlightRenderer::drawLightenSlot);
        }

        if(KeyHandler.renderModifiedTooltip() || KeyHandler.renderFullTooltip()) {
            if(ForgeConfigHandler.client.inventoryTooltip.leftOverRight) {
                frontRender.sort((left, right) -> right.xPos - left.xPos);
            }
            if(ForgeConfigHandler.client.inventoryTooltip.topOverBottom) {
                frontRender.sort((left, right) -> right.yPos - left.yPos);
            }
            frontRender.forEach(slot -> {
                boolean renderSlot = true;
                switch (ForgeConfigHandler.client.inventoryTooltip.keyBindBehavior){
                    case VERTICAL:
                        renderSlot = guiContainer.getSlotUnderMouse() != null && guiContainer.getSlotUnderMouse().xPos == slot.xPos;
                        break;
                    case HORIZONTAL:
                        renderSlot = guiContainer.getSlotUnderMouse() != null && guiContainer.getSlotUnderMouse().yPos == slot.yPos;
                        break;
                }
                if(renderSlot){
                    switch (ForgeConfigHandler.client.inventoryTooltip.hideHorizontal){
                        case LEFT:
                            renderSlot = guiContainer.getSlotUnderMouse() != null && guiContainer.getSlotUnderMouse().xPos <= slot.xPos;
                            break;
                        case RIGHT:
                            renderSlot = guiContainer.getSlotUnderMouse() != null && guiContainer.getSlotUnderMouse().xPos >= slot.xPos;
                            break;
                    }
                }
                if(renderSlot){
                    switch (ForgeConfigHandler.client.inventoryTooltip.hideVertical){
                        case TOP:
                            renderSlot = guiContainer.getSlotUnderMouse() != null && guiContainer.getSlotUnderMouse().yPos <= slot.yPos;
                            break;
                        case BOTTOM:
                            renderSlot = guiContainer.getSlotUnderMouse() != null && guiContainer.getSlotUnderMouse().yPos >= slot.yPos;
                            break;
                    }
                }

                if(renderSlot){
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(
                            slot.xPos + 8,
                            slot.yPos + 8,
                            0
                    );
                    GlStateManager.rotate(ForgeConfigHandler.client.inventoryTooltip.rotationAngle, 0.0F, 0.0F, 1.0F);
                    float width = 0F;
                    List<String> tooltip = JEIUtil.getDesirableTooltip(slot.getStack());
                    if(ForgeConfigHandler.client.inventoryTooltip.posLeftSide) {
                        for (String s : tooltip) width = Math.max(width, mc.fontRenderer.getStringWidth(s));
                        width += 24;
                    }
                    GlStateManager.translate(
                            ForgeConfigHandler.client.inventoryTooltip.xOffset - width,
                            -ForgeConfigHandler.client.inventoryTooltip.yOffset,
                            0
                    );
                    GuiUtils.drawHoveringText(
                            slot.getStack(),
                            JEIUtil.getDesirableTooltip(slot.getStack()),
                            0,
                            0,
                            mc.displayWidth,
                            mc.displayHeight,
                            -1,
                            mc.fontRenderer
                    );
                    GlStateManager.popMatrix();
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
        int color = ForgeConfigProvider.getSignedHexadecimal(ForgeConfigHandler.client.inventoryDarkBase) + ForgeConfigHandler.client.inventoryDarkAlpha.value;
        GuiUtils.drawGradientRect(
                0,
                slot.xPos,
                slot.yPos,
                slot.xPos + 16,
                slot.yPos + 16,
                color,
                color
        );
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
    }

    public static void drawLightenSlot(Slot slot) {
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.colorMask(true, true, true, false);
        int color = ForgeConfigProvider.getSignedHexadecimal(ForgeConfigHandler.client.inventoryLightBase) + ForgeConfigHandler.client.inventoryLightAlpha.value;
        GuiUtils.drawGradientRect(
                0,
                slot.xPos,
                slot.yPos,
                slot.xPos + 16,
                slot.yPos + 16,
                color,
                color
        );
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
    }

    public static void drawBorderSlot(Slot slot, ItemStack itemStack) {
        renderStackBackground = true;
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
        renderStackBackground = false;
    }
}
