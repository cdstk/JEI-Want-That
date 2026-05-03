package jeiwt.compat;

import antiqueatlasautomarker.config.ConfigHandler;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerRenderInfo;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import jeiwt.client.handlers.KeyHandler;
import jeiwt.client.renderer.WorldTooltipRenderer;
import jeiwt.handlers.ForgeConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AAAMHandler {

    private static final Map<Marker, Double> markersToRender = new HashMap<>();

    public static List<String> tryGettingMarkerText(EntityVillager villager) {
        List<String> tooltip = new ArrayList<>();

        EntityPlayer player = Minecraft.getMinecraft().player;
        List<Integer> atlases = AtlasAPI.getPlayerAtlases(player);
        if(atlases.isEmpty()) return tooltip;

        int atlasID = atlases.get(0);
        List<Marker> markersAtPosition = AntiqueAtlasMod.markersData
                .getMarkersData(atlasID, player.world)
                .getMarkersDataInDimension(player.dimension)
                .getAllMarkers()
                .stream()
                .filter(marker -> marker.getType().equals(ConfigHandler.enchantments.marker))
                .filter(marker -> Math.abs(marker.getX() - villager.posX) < ForgeConfigHandler.modComptability.aaam.villagerDistance
                                && Math.abs(marker.getZ() - villager.posZ) < ForgeConfigHandler.modComptability.aaam.villagerDistance)
                .collect(Collectors.toList());

        if(markersAtPosition.isEmpty()) return tooltip;
        markersAtPosition.sort(Comparator.comparingDouble(left -> villager.getDistance(left.getX(), 0, left.getZ())));

        Marker selectedMarker = markersAtPosition.get(0);
        if(ForgeConfigHandler.modComptability.aaam.villagerLineOfSight) {
            Vec3d villagerVec = new Vec3d(villager.posX, villager.posY + villager.getEyeHeight(), villager.posZ);
            for(Marker possible : markersAtPosition) {
                double x = possible.getX() + 0.5D;
                double y = villager.posY + villager.getEyeHeight();
                double z = possible.getZ() + 0.5D;

                RayTraceResult rayTraceToVillager = villager.getWorld().rayTraceBlocks(
                        new Vec3d(x, y, z),
                        villagerVec,
                        false,
                        true,
                        false
                );
                if(rayTraceToVillager == null) {
                    selectedMarker = possible;
                    break;
                }
            }
        }
        if(ForgeConfigHandler.modComptability.aaam.enchantmentMarkers) {
            markersToRender.putIfAbsent(selectedMarker, villager.posY);
        }
        if(!ForgeConfigHandler.modComptability.aaam.villagerMarkers) {
            return tooltip;
        }

        String[] split = selectedMarker.getLabel().split(", ");
        tooltip.addAll(Arrays.asList(split));
        return tooltip;
    }

    public static void renderMarkers() {
        markersToRender.forEach(AAAMHandler::renderMarker);
        markersToRender.clear();
    }

    private static void renderMarker(Marker marker, double markerY) {
        Minecraft mc = Minecraft.getMinecraft();
        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;
        double posX = marker.getX() + 0.5 - camX;
        double posY = markerY - camY;
        double posZ = marker.getZ() + 0.5 - camZ;

        double distance = mc.player.getDistance(marker.getX() + 0.5D, markerY, marker.getZ() + 0.5D);
        float baseScale = ForgeConfigHandler.client.baseScale; // 0.025F;
        float distanceScale = 1F;
        if(distance >= ForgeConfigHandler.client.distanceScaleStart) {
            distanceScale = (float) (ForgeConfigHandler.client.distanceScaleFactor * distance);
        }

        renderTooltip(marker,
                posX,
                posY,
                posZ,
                baseScale,
                distanceScale,
                mc
        );
    }

    public static void renderTooltip(Marker marker, double posX, double posY, double posZ, float baseScale, float distanceScale, Minecraft mc) {
        List<String> blankLines = new ArrayList<>();
        boolean renderPole = true;

        int width = 0;
        int drawX = -12;
        int drawY = 0;
        float poleHeight = ForgeConfigHandler.modComptability.aaam.markerPoleHeight;
        if(ForgeConfigHandler.modComptability.aaam.markerPoleHeightEye) poleHeight += Minecraft.getMinecraft().player.getEyeHeight();
        ItemStack stack = new ItemStack(AtlasAPI.ATLAS_ITEM);
        List<String> tooltip = Arrays.asList(marker.getLabel().split(", "));
        for (String s : tooltip) width = Math.max(width, mc.fontRenderer.getStringWidth(s));
        if(baseScale != 0){
            poleHeight *= (0.025F / baseScale);
        }

        poleHeight += 0.25F * tooltip.size();
        if (baseScale != 0) {
            poleHeight *= (0.025F / baseScale);
        }

        if(ForgeConfigHandler.client.poleDisable){
            renderPole = false;
            poleHeight = 0;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, posZ);
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) GlStateManager.rotate(-180F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        if(ForgeConfigHandler.modComptability.aaam.worldHingePoint == ForgeConfigHandler.ClientConfig.HingePoint.BASE) {
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        }
        if(KeyHandler.renderModifiedTooltip()){
            switch (ForgeConfigHandler.client.worldModifyKey){
                case HIDE_POLE:
                    renderPole = false;
                    break;
                case MOVE_UP_ALL:
                    GlStateManager.translate(0, poleHeight / 2, 0);
                    break;
                case MOVE_UP_POLE:
                    GlStateManager.translate(0, poleHeight / 2, 0);
                    poleHeight /= 2;
                    break;
            }
        }
        GlStateManager.translate(0, poleHeight * (baseScale / 0.025F), 0.1);
        GlStateManager.scale(baseScale, baseScale, baseScale);

        if(renderPole) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(-ForgeConfigHandler.client.poleScale, -1, 0);
            for(float i = 0.25F; i < poleHeight; i += 0.25F) blankLines.add(" ");
            GuiUtils.drawHoveringText(
                    stack,
                    blankLines,
                    (int) (-mc.fontRenderer.getStringWidth(" ") * 3.5),
                    drawY,
                    mc.displayWidth,
                    mc.displayHeight,
                    -1,
                    mc.fontRenderer
            );
            GlStateManager.popMatrix();
        }

        GlStateManager.disableRescaleNormal();
        WorldTooltipRenderer.enableWorldOverlayStandardItemLighting();

        if(ForgeConfigHandler.modComptability.aaam.worldHingePoint == ForgeConfigHandler.ClientConfig.HingePoint.INFO) {
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        }

        MarkerType type = MarkerRegistry.find(marker.getType());
        boolean renderLabel = ForgeConfigHandler.modComptability.aaam.markersSwapFullBehavior != KeyHandler.renderFullTooltip();
        if(type != null) {
            GlStateManager.pushMatrix();
            if(ForgeConfigHandler.client.worldIconsIgnoreDepth) GlStateManager.disableDepth();
            GlStateManager.scale(
                    -distanceScale * ForgeConfigHandler.modComptability.aaam.markerIconScale,
                    -distanceScale * ForgeConfigHandler.modComptability.aaam.markerIconScale,
                    -0.001
            );
            int screenScale = new ScaledResolution(mc).getScaleFactor();
            type.calculateMip(1, 1, screenScale);
            MarkerRenderInfo info = type.getRenderInfo(1, 1, screenScale);
            type.resetMip();
            AtlasRenderHelper.drawFullTexture(
                    info.tex,
                    drawX + info.x + ForgeConfigHandler.modComptability.aaam.xMarkerIconOffset,
                    drawY + info.y - ForgeConfigHandler.modComptability.aaam.yMarkerIconOffset,
                    info.width, info.height);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        if(renderLabel) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-ForgeConfigHandler.client.xWorldOffset, ForgeConfigHandler.client.yWorldOffset, 0);
            GlStateManager.scale(-distanceScale, -distanceScale, -0.001);
            if (!marker.getLabel().isEmpty()) {
                drawX += (-width / 2);
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
        }

        GlStateManager.enableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
