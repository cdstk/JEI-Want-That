package jeiwt.client.renderer;

import com.google.common.base.Predicates;
import jeiwt.client.handlers.KeyHandler;
import jeiwt.handlers.ForgeConfigHandler;
import jeiwt.util.JEIUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class WorldTooltipRenderer {

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;
        if (!KeyHandler.isKeyDown(KeyHandler.enableDisplay)) return;

        RayTraceResult mouseOverItem = getEntityItem(mc.player, event.getPartialTicks(), mc);
        List<EntityItem> itemEntities = mc.world.getEntities(EntityItem.class, entityItem -> mc.player.canEntityBeSeen(entityItem));
        itemEntities.removeIf(entityItem -> !JEIUtil.isItemStackDesirable(entityItem.getItem()));
        itemEntities.sort((left, right) -> {
            BlockPos pos = mc.player.getPosition();
            if(mouseOverItem != null){
                switch (mouseOverItem.typeOfHit){
                    case ENTITY:
                        if(mouseOverItem.entityHit != null) pos = mouseOverItem.entityHit.getPosition();
                        break;
                    case BLOCK:
                        if(mouseOverItem.getBlockPos() != null) pos = mouseOverItem.getBlockPos();
                        break;
                }
            }
            double delta = right.getDistanceSq(pos) - left.getDistanceSq(pos);
            if (delta > 0) return (int) Math.max(1, delta);
            else if (delta < 0) return (int) Math.min(-1, delta);
            return 0;
        });

        itemEntities.forEach(entityItem -> renderItem(entityItem, event.getPartialTicks()));
    }

    // https://github.com/CreativeMD/ItemPhysic/blob/1.12/src/main/java/com/creativemd/itemphysic/EventHandler.java#L82
    public static RayTraceResult getEntityItem(EntityPlayer player, Vec3d position, Vec3d look, double distance) {
        Vec3d include = look.subtract(position);
        List<Entity> list = player.world.getEntitiesInAABBexcluding(
                player,
                player.getEntityBoundingBox()
                        .expand(include.x, include.y, include.z)
                        .expand(distance, distance, distance),
                Predicates.and(EntitySelectors.NOT_SPECTATING, entity -> entity instanceof EntityItem)
        );
        for (Entity entity : list) {
                AxisAlignedBB aabb = entity.getEntityBoundingBox().grow(0.2);
                RayTraceResult movingObjectPosition = aabb.calculateIntercept(position, look);

                if (movingObjectPosition != null) {
                    movingObjectPosition.typeOfHit = RayTraceResult.Type.ENTITY;
                    movingObjectPosition.entityHit = entity;
                    return movingObjectPosition;
                }
                else if (aabb.contains(position)) {
                    return new RayTraceResult(entity);
                }
        }
        return null;
    }

    // https://github.com/CreativeMD/ItemPhysic/blob/1.12/src/main/java/com/creativemd/itemphysic/EventHandler.java#L82
    public static RayTraceResult getEntityItem(EntityPlayer player, float partialTicks, Minecraft mc) {
        double distance = ForgeConfigHandler.client.mouseTargetRange;
        Vec3d position = player.getPositionEyes(partialTicks);
        Vec3d vec3d1 = player.getLook(partialTicks);
        Vec3d look = position.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);

        RayTraceResult other = mc.world.rayTraceBlocks(position, look, false, true, false);

        if (other != null) {
            if (other.typeOfHit == RayTraceResult.Type.BLOCK) {
                distance = Math.min(distance, position.distanceTo(other.hitVec));
            } else if (other.typeOfHit == RayTraceResult.Type.ENTITY) {
                distance = Math.min(distance, other.entityHit.getDistance(position.x, position.y, position.z));
            }
        }

        return getEntityItem(player, position, position.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance), distance);

    }

    private static void renderItem(EntityItem entity, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;
        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - camX;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - camY;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - camZ;

        FontRenderer font = mc.fontRenderer;
        ItemStack stack = entity.getItem();
        List<String> tooltip = JEIUtil.getDesirableTooltip(stack);
        List<String> blankLines = new ArrayList<>();

        double distance = mc.player.getDistance(entity);
        float baseScale = ForgeConfigHandler.client.baseScale; // 0.025F;
        float distanceScaler = 1F;
        boolean renderItemStack = tooltip.isEmpty() && ForgeConfigHandler.client.emptyTooltipRender == ForgeConfigHandler.ClientConfig.EmptyTooltipRender.ITEM_STACK;
        if(distance >= ForgeConfigHandler.client.distanceScaleStart) {
            distanceScaler = (float) (ForgeConfigHandler.client.distanceScaleFactor * distance);
        }

        int width = 0;
        for(String s : tooltip) width = Math.max(width, font.getStringWidth(s));
        int drawX = (-width/2) - 12;
        int drawY = 0;

        float poleHeight = 1F;
        poleHeight += 0.25F * tooltip.size();
        for(float i = 0.25F; i < poleHeight; i += 0.25F) blankLines.add(" ");

        GlStateManager.pushMatrix();

        GlStateManager.translate(posX, posY, posZ);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        if(KeyHandler.isKeyDown(KeyHandler.modifiedTooltip)){
            GlStateManager.translate(0, 1F, 0);
        }
        GlStateManager.translate(0, poleHeight, 0.1);
        GlStateManager.scale(-baseScale, -baseScale, baseScale);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1, 1, 0);
        GuiUtils.drawHoveringText(
                stack,
                blankLines,
                (int) (-font.getStringWidth(" ") * 3.5),
                drawY,
                mc.displayWidth,
                mc.displayHeight,
                -1,
                mc.fontRenderer
        );
        if(!renderItemStack) {
            GlStateManager.translate(0, -ForgeConfigHandler.client.yInventoryOffset, 0);
            GlStateManager.scale(distanceScaler, distanceScaler, distanceScaler);
            GuiUtils.drawHoveringText(
                    stack,
                    tooltip,
                    drawX,
                    drawY,
                    mc.displayWidth,
                    mc.displayHeight,
                    -1,
                    mc.fontRenderer
            );
        }
        GlStateManager.popMatrix();

        if(renderItemStack){
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableDepth();
            drawX += ForgeConfigHandler.client.xWorldOffset + 1;
            drawY -= ForgeConfigHandler.client.yWorldOffset - 4;
            GlStateManager.scale(distanceScaler, distanceScaler, 1);
            GuiUtils.drawContinuousTexturedBox(
                    new ResourceLocation("textures/gui/widgets.png"),
                    drawX,
                    drawY,
                    24,
                    22,
                    29,
                    24,
                    29,
                    24,
                    0,
                    0
            );
            GlStateManager.scale(1, 1, -1.0e-4F);
            drawItemStack(
                    stack,
                    drawX,
                    drawY,
                    font
            );
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    // GuiIngame.renderHotbar()
    private static void drawItemStack(ItemStack stack, int mouseX, int mouseY, FontRenderer font){
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, mouseX + 3, mouseY + 4);
        mc.getRenderItem().renderItemOverlayIntoGUI(font, stack, mouseX + 3, mouseY + 4, null);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
}