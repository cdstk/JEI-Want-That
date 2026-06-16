package jeiwt.client.renderer;

import com.google.common.base.Predicates;
import jeiwt.JEIWantThat;
import jeiwt.capability.WorldTooltipOverride.IWorldTooltipOverride;
import jeiwt.capability.WorldTooltipOverride.WorldTooltipOverrideHandler;
import jeiwt.client.handlers.KeyHandler;
import jeiwt.compat.AAAMHandler;
import jeiwt.compat.CharmUtil;
import jeiwt.compat.ModLoadedUtil;
import jeiwt.compat.WaystonesUtil;
import jeiwt.compat.WearableBackpacksUtil;
import jeiwt.handlers.ForgeConfigHandler;
import jeiwt.handlers.ForgeConfigProvider;
import jeiwt.mixin.vanilla.RenderGlobal_InvokerMixin;
import jeiwt.util.JEIUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WorldTooltipRenderer {

    // TODO refactor for craft tweaker support/clarity, bloated since all the additions
    // The render translation and distance scale orders for each Gui draw method are all different, FIX
    // Shared copy pasted GL calls, FIX
    // Keybind render modifiers are copy pasted, FIX
    // Magic Numbers used to center, FIX

    private static Vec3d clientView = null;

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;
        if (!mc.world.isRemote) return;
        if (!KeyHandler.renderDisplay()) return;

        if(mc.renderGlobal instanceof RenderGlobal_InvokerMixin) {
            Vector3f vector3f = ((RenderGlobal_InvokerMixin) mc.renderGlobal).invokeGetViewVector(mc.player, event.getPartialTicks());
            clientView = new Vec3d(vector3f.getX(), vector3f.getY(), vector3f.getZ()).normalize();
        }
        else {
            clientView = mc.player.getLookVec().normalize();
        }


        RayTraceResult mouseOverEntity = getMouseOverEntity(mc.player, event.getPartialTicks(), mc);
        List<Entity> entitiesToRender = new ArrayList<>();
        entitiesToRender.addAll(mc.world.getEntities(EntityItem.class, entityItem -> canShowToPlayer(mc.player, entityItem)));
        entitiesToRender.addAll(mc.world.getEntities(EntityVillager.class, entityVillager -> ForgeConfigHandler.villagerSearch.enabled));
        JEIUtil.getDesirableEntities().forEach(clazz -> entitiesToRender.addAll(mc.world.getEntities(clazz, entity -> !(entity instanceof EntityVillager) && canShowToPlayer(mc.player, entity))));

        entitiesToRender.removeIf(entity -> {
            Vec3d target = new Vec3d(entity.posX + 0.5, entity.posY + 0.5, entity.posZ + 0.5);
            return !isTargetInViewCone(mc.player, clientView, target, mc.gameSettings.fovSetting + ForgeConfigHandler.client.fovModifier);
        });
        entitiesToRender.removeIf(entity -> !isEntityDesirable(entity));
        entitiesToRender.sort((left, right) -> {
            BlockPos pos = mc.player.getPosition();
            if(mouseOverEntity != null){
                if (mouseOverEntity.typeOfHit == RayTraceResult.Type.ENTITY && mouseOverEntity.entityHit != null) {
                    pos = mouseOverEntity.entityHit.getPosition();
                }
                else {
                    pos = mouseOverEntity.getBlockPos();
                }
            }
            double delta = right.getDistanceSq(pos) - left.getDistanceSq(pos);
            if (delta > 0) return (int) Math.max(1, delta);
            else if (delta < 0) return (int) Math.min(-1, delta);
            return 0;
        });

        List<TileEntity> tileEntitiesToRender = new ArrayList<>();
        if(ForgeConfigHandler.tileEntitySearch.enabled) tileEntitiesToRender.addAll(mc.world.loadedTileEntityList);
        tileEntitiesToRender.removeIf(entity -> {
            Vec3d target = new Vec3d(entity.getPos().getX() + 0.5, entity.getPos().getY() + 0.5, entity.getPos().getZ() + 0.5);
            return !isTargetInViewCone(mc.player, clientView, target, mc.gameSettings.fovSetting + ForgeConfigHandler.client.fovModifier);
        });
        tileEntitiesToRender.removeIf(tileEntity -> !isTileEntityDesirable(tileEntity, mc.player));
        tileEntitiesToRender.sort((left, right) -> {
            BlockPos pos = mc.player.getPosition();
            if(mouseOverEntity != null){
                if (mouseOverEntity.typeOfHit == RayTraceResult.Type.ENTITY && mouseOverEntity.entityHit != null) {
                    pos = mouseOverEntity.entityHit.getPosition();
                }
                else {
                    pos = mouseOverEntity.getBlockPos();
                }
            }
            double delta = right.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) - left.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
            if (delta > 0) return (int) Math.max(1, delta);
            else if (delta < 0) return (int) Math.min(-1, delta);
            return 0;
        });

        List<EntityPlayer> playerToRender = new ArrayList<>();
        playerToRender.addAll(mc.world.getEntities(EntityPlayer.class, entityPlayer ->
                entityPlayer != mc.player
                        && ForgeConfigHandler.playerSearch.enabled
                        && !entityPlayer.isInvisibleToPlayer(mc.player)
                        && !entityPlayer.isSneaking()
        ));
        playerToRender.sort((left, right) -> {
            BlockPos pos = mc.player.getPosition();
            if(mouseOverEntity != null){
                if (mouseOverEntity.typeOfHit == RayTraceResult.Type.ENTITY && mouseOverEntity.entityHit != null) {
                    pos = mouseOverEntity.entityHit.getPosition();
                }
                else {
                    pos = mouseOverEntity.getBlockPos();
                }
            }
            double delta = right.getDistanceSq(pos) - left.getDistanceSq(pos);
            if (delta > 0) return (int) Math.max(1, delta);
            else if (delta < 0) return (int) Math.min(-1, delta);
            return 0;
        });

        JEIWantThat.setSkipModdedTooltips();
        // Renders First
        tileEntitiesToRender.forEach(tileEntity -> renderTileEntity(tileEntity, event.getPartialTicks()));
        entitiesToRender.forEach(entity -> renderEntity(entity, event.getPartialTicks())); // TODO split items, villagers, and entities
        if(ModLoadedUtil.AAAM.isLoaded()) AAAMHandler.renderMarkers();
        playerToRender.forEach(entity -> renderEntity(entity, event.getPartialTicks()));
        // Renders Last
        JEIWantThat.resetSkipModdedTooltips();
    }

    public static boolean canShowToPlayer(EntityPlayer fromEntity, Entity toEntity) {
        return fromEntity.canEntityBeSeen(toEntity) || fromEntity.getDistance(toEntity) <= fromEntity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 5F;
    }

    public static boolean isEntityDesirable(Entity entity){
        if(entity instanceof EntityItem
                && JEIUtil.isItemStackDesirable(((EntityItem) entity).getItem())) {
            return true;
        }
        else if(entity instanceof EntityVillager
                && ForgeConfigProvider.checkVillagerProfession((EntityVillager)entity)){
            return true;
        }
        return ForgeConfigHandler.entitySearch.enabled && JEIUtil.getDisplayForEntity(entity) != ItemStack.EMPTY;
    }

    public static boolean isTileEntityDesirable(TileEntity tileEntity, EntityPlayer player){
        if(!tileEntity.getWorld().getBlockState(tileEntity.getPos()).getBlock().equals(tileEntity.getBlockType())) return false;

        boolean needsSight = true;
        if(tileEntity instanceof TileEntityShulkerBox) needsSight = !ForgeConfigHandler.tileEntitySearch.shulkerBoxes;
        else if(ModLoadedUtil.CHARM.isLoaded() && CharmUtil.isTileUnsealedCrate(tileEntity)) needsSight = !ForgeConfigHandler.tileEntitySearch.charmCrates;
        else if(ModLoadedUtil.WEARABLE_BACKPACKS.isLoaded() && WearableBackpacksUtil.isTileBackpack(tileEntity)) needsSight = !ForgeConfigHandler.tileEntitySearch.wearableBackpacks;
        else if(ModLoadedUtil.WAYSTONES.isLoaded() && WaystonesUtil.isTileWaystone(tileEntity)) {
            IBlockState checkState = tileEntity.getWorld().getBlockState(tileEntity.getPos());
            TileEntity checkTile = tileEntity;
            boolean isDummy = WaystonesUtil.isBlockWaystone(checkState.getBlock()) && !WaystonesUtil.isWaystoneBase(checkState);

            if(isDummy){
                BlockPos belowPos = new BlockPos(tileEntity.getPos().getX(), tileEntity.getPos().getY() - 1, tileEntity.getPos().getZ());
                TileEntity belowTile = tileEntity.getWorld().getTileEntity(belowPos);
                if(WaystonesUtil.isTileWaystone(belowTile)) {
                    checkState = tileEntity.getWorld().getBlockState(belowPos);
                    checkTile = belowTile;
                }
            }

            if(WaystonesUtil.isBlockWaystone(checkState.getBlock())) {
                if(WaystonesUtil.isWaystoneKnown(checkTile)) {
                    if(ForgeConfigHandler.tileEntitySearch.waystonesKnown) {
                        if(isDummy) return false;
                        needsSight = false;
                    }
                }
                if(WaystonesUtil.isWaystoneNatural(checkTile)) {
                    if(ForgeConfigHandler.tileEntitySearch.waystonesNatural) {
                        if(isDummy) return false;
                        needsSight = false;
                    }
                }
                if(!isDummy && needsSight) return false;
            }
        }


        // Middle Mouse Pick Block
        ItemStack tileStack = ItemStack.EMPTY;
        // Charm Crate states were being queried on air blocks
        try {
            tileStack = tileEntity.getBlockType().getPickBlock(
                    tileEntity.getWorld().getBlockState(tileEntity.getPos()),
                    new RayTraceResult(player),
                    tileEntity.getWorld(),
                    tileEntity.getPos(),
                    player
            );
        }
        catch (Exception exception) {
            JEIWantThat.LOGGER.log(Level.WARN, "Failed to getPickBlock from Tile Entity: {}", exception.toString());
            return false;
        }

        boolean match = false;
        if(tileStack.getItem() != Items.AIR){
            Minecraft.getMinecraft().storeTEInStack(tileStack, tileEntity);
            match = JEIUtil.isItemStackDesirable(tileStack, false);
        }

        if(match && needsSight){
            RayTraceResult rayTraceBlocks = tileEntity.getWorld().rayTraceBlocks(
                    new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ),
                    new Vec3d(tileEntity.getPos().getX() + 0.5, tileEntity.getPos().getY() + 0.5, tileEntity.getPos().getZ() + 0.5),
                    false,
                    true,
                    false);
            if(rayTraceBlocks == null || !rayTraceBlocks.getBlockPos().equals(tileEntity.getPos())) {
                match = false;
            }
        }

        return match;
    }

    public static boolean isTargetInViewCone(EntityPlayer player, Vec3d clientView, Vec3d target, float fovDegrees) {
        Vec3d toTarget = target.subtract(player.getPositionEyes(1.0F)).normalize();

        double dot = clientView.dotProduct(toTarget);

        // Convert FOV to cosine threshold
        double threshold = Math.cos(Math.toRadians(fovDegrees / 2.0));

        return dot > threshold;
    }

    // https://github.com/CreativeMD/ItemPhysic/blob/1.12/src/main/java/com/creativemd/itemphysic/EventHandler.java#L82
    public static RayTraceResult getMouseOverEntity(EntityPlayer player, Vec3d position, Vec3d look, double distance) {
        Vec3d include = look.subtract(position);
        List<Entity> list = player.world.getEntitiesInAABBexcluding(
                player,
                player.getEntityBoundingBox()
                        .expand(include.x, include.y, include.z)
                        .expand(distance, distance, distance),
                Predicates.and(EntitySelectors.NOT_SPECTATING, WorldTooltipRenderer::isEntityDesirable)
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
        return null; // Returns null or ENTITY
    }

    // https://github.com/CreativeMD/ItemPhysic/blob/1.12/src/main/java/com/creativemd/itemphysic/EventHandler.java#L82
    public static RayTraceResult getMouseOverEntity(EntityPlayer player, float partialTicks, Minecraft mc) {
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

        RayTraceResult possibleEntity = getMouseOverEntity(player, position, position.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance), distance);
        if(possibleEntity != null) return possibleEntity;
        return ForgeConfigHandler.client.mouseTargetBlock ? other : null; // NULL, MISS, BLOCK
    }

    private static void renderTileEntity(TileEntity tileEntity, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos pos = tileEntity.getPos();
        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;
        double posX = pos.getX() + 0.5 - camX;
        double posY = pos.getY() - camY;
        double posZ = pos.getZ() + 0.5 - camZ;

        double distance = mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        float baseScale = ForgeConfigHandler.client.baseScale; // 0.025F;
        float distanceScale = 1F;
        if(distance >= ForgeConfigHandler.client.distanceScaleStart) {
            distanceScale = (float) (ForgeConfigHandler.client.distanceScaleFactor * distance);
        }

        int width = 0;
        int drawX = -12;
        int drawY = 0;
        float poleHeight = 1.75F;
        if(baseScale != 0){
            poleHeight *= (0.025F / baseScale);
        }
        ItemStack stack = tileEntity.getBlockType().getPickBlock(
                tileEntity.getWorld().getBlockState(tileEntity.getPos()),
                new RayTraceResult(mc.player),
                tileEntity.getWorld(),
                tileEntity.getPos(),
                mc.player
        );
        List<String> tooltip = new ArrayList<>();
        if(ForgeConfigHandler.client.emptyTooltipRender == ForgeConfigHandler.ClientConfig.EmptyTooltipRender.DISPLAY_NAME) tooltip.add(stack.getDisplayName());

        for (String s : tooltip) width = Math.max(width, mc.fontRenderer.getStringWidth(s));
        drawX += (-width / 2);
        poleHeight += 0.25F * tooltip.size();
        if (baseScale != 0) {
            poleHeight *= (0.025F / baseScale);
        }
        renderTooltip(
                stack,
                tooltip,
                posX,
                posY,
                posZ,
                drawX,
                drawY,
                poleHeight,
                baseScale,
                distanceScale,
                mc
        );
    }

    private static void renderEntity(Entity entity, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;
        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - camX;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - camY;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - camZ;

        double distance = mc.player.getDistance(entity);
        float baseScale = ForgeConfigHandler.client.baseScale; // 0.025F;
        float distanceScale = 1F;
        if(distance >= ForgeConfigHandler.client.distanceScaleStart) {
            distanceScale = (float) (ForgeConfigHandler.client.distanceScaleFactor * distance);
        }

        int width = 0;
        int drawX = -12;
        int drawY = 0;
        float poleHeight = 0;
        ItemStack stack = ItemStack.EMPTY;
        String displayName = "";
        List<String> tooltip = new ArrayList<>();

        IWorldTooltipOverride tooltipOverride = entity.getCapability(WorldTooltipOverrideHandler.WORLD_TOOLTIP, null);
        if(tooltipOverride != null) {
            if(!tooltipOverride.getDisplayName().isEmpty()) displayName = tooltipOverride.getDisplayName();
            if(!tooltipOverride.getDescription().isEmpty()) tooltip.add(tooltipOverride.getDescription());
        }

        if(entity instanceof EntityItem) {
            stack = ((EntityItem) entity).getItem();
            displayName = ""; // Stack
            tooltip.addAll(JEIUtil.getDesirableTooltip(stack));
            poleHeight = 1F;
        }
        else {
            if(entity instanceof EntityVillager) {
                EntityVillager villager = (EntityVillager) entity;
                stack = ForgeConfigProvider.getVillagerTooltipItem();
                if(ModLoadedUtil.AAAM.isLoaded()
                        && villager.getProfessionForge().getRegistryName().toString().equals("minecraft:librarian")
                        && !(displayName.contains(I18n.format("entity.Villager.cartographer")))
                ) {
                    tooltip.clear();
                    tooltip.addAll(AAAMHandler.tryGettingMarkerText(villager));
                }
                if(ForgeConfigProvider.checkVillagerProfession(villager)) {
                    if (ForgeConfigHandler.villagerSearch.worldSwapFullBehavior == KeyHandler.renderFullTooltip()) {
                        if(displayName.isEmpty()) {
                            displayName = entity.hasCustomName()
                                    ? entity.getDisplayName().getFormattedText()
                                    : entity.getName();
                        }
                    }
                    else {
                        if(displayName.isEmpty()) {
                            displayName = ((EntityVillager) entity).getProfessionForge().getRegistryName().toString();
                        }
                    }
                }
            }
            else if(entity instanceof EntityPlayer) {
                drawPlayerHead(
                        (EntityPlayer) entity,
                        posX,
                        posY,
                        posZ,
                        baseScale,
                        distanceScale,
                        mc
                );
                return;
            }
            else {
                stack = JEIUtil.getDisplayForEntity(entity);
                if((KeyHandler.renderFullTooltip() && ForgeConfigHandler.entitySearch.worldSwapFullBehavior)
                    || (!KeyHandler.renderFullTooltip() && !ForgeConfigHandler.entitySearch.worldSwapFullBehavior)) {
                    if(displayName.isEmpty()) {
                        displayName = entity.hasCustomName()
                                ? entity.getDisplayName().getFormattedText()
                                : entity.getName();
                    }
                }
            }
            poleHeight = entity.height;
        }
        if(!displayName.isEmpty())
            tooltip.add(0, displayName);

        for (String s : tooltip) width = Math.max(width, mc.fontRenderer.getStringWidth(s));
        drawX += (-width / 2);
        poleHeight += 0.25F * tooltip.size();
        if (baseScale != 0) {
            poleHeight *= (0.025F / baseScale);
        }
        renderTooltip(
                stack,
                tooltip,
                posX,
                posY,
                posZ,
                drawX,
                drawY,
                poleHeight,
                baseScale,
                distanceScale,
                mc
        );
    }

    public static void renderTooltip(@Nonnull final ItemStack stack, List<String> textLines, double posX, double posY, double posZ, int drawX, int drawY, float poleHeight, float baseScale, float distanceScale, Minecraft mc) {
        List<String> blankLines = new ArrayList<>();
        boolean renderPole = true;
        if(ForgeConfigHandler.client.poleDisable){
            renderPole = false;
            poleHeight = 0;
        }

        boolean renderItemStack = textLines.isEmpty();
        if(ForgeConfigHandler.client.emptyTooltipRender == ForgeConfigHandler.ClientConfig.EmptyTooltipRender.NONE) renderItemStack = false;

        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, posZ);
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) GlStateManager.rotate(-180F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        if(ForgeConfigHandler.client.worldHingePoint == ForgeConfigHandler.ClientConfig.HingePoint.BASE) {
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
        enableWorldOverlayStandardItemLighting();
        if(ForgeConfigHandler.client.worldHingePoint == ForgeConfigHandler.ClientConfig.HingePoint.INFO) {
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        }
        if(renderItemStack) {
            GlStateManager.pushMatrix();
            if(ForgeConfigHandler.client.worldIconsIgnoreDepth) GlStateManager.disableDepth();
            GlStateManager.scale(-distanceScale, -distanceScale, -0.001);
            drawX += ForgeConfigHandler.client.xWorldOffset - 6 + (int) (mc.fontRenderer.getStringWidth(" ") * 3.5F / 2F);
            drawY -= ForgeConfigHandler.client.yWorldOffset - 4;
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
            drawItemStack(
                    stack,
                    drawX,
                    drawY,
                    mc.fontRenderer
            );
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        // TODO Why do some TE like Shulker/Chest cull but others like Charm Crate alwys render
        else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-ForgeConfigHandler.client.xWorldOffset, ForgeConfigHandler.client.yWorldOffset, 0);
            GlStateManager.scale(-distanceScale, -distanceScale, -0.001);
            GuiUtils.drawHoveringText(
                    stack,
                    textLines,
                    drawX,
                    drawY,
                    mc.displayWidth,
                    mc.displayHeight,
                    -1,
                    mc.fontRenderer
            );
            GlStateManager.popMatrix();
        }
        GlStateManager.enableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    // TODO Find angle matching RenderHelper.enableGUIStandardItemLighting()
    public static void enableWorldOverlayStandardItemLighting() {
        GlStateManager.pushMatrix();
//        GlStateManager.rotate(0, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-45, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    // GuiIngame.renderHotbar()
    public static void drawItemStack(ItemStack stack, int mouseX, int mouseY, FontRenderer font){
        Minecraft mc = Minecraft.getMinecraft();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, mouseX + 3, mouseY + 4);
        mc.getRenderItem().renderItemOverlayIntoGUI(font, stack, mouseX + 3, mouseY + 4, null);
    }

    // TODO Render like Nametag and pivot from there
    public static void drawPlayerHead(EntityPlayer entityPlayer, double posX, double posY, double posZ, float baseScale, float distanceScale, Minecraft mc) {
        NetworkPlayerInfo networkPlayerInfo = mc.player.connection.getPlayerInfo(entityPlayer.getUniqueID());
        if(networkPlayerInfo == null) return;

        boolean wearingHat = entityPlayer.isWearing(EnumPlayerModelParts.HAT);

        GlStateManager.pushMatrix();

        GlStateManager.translate(posX, posY, posZ);
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) GlStateManager.rotate(-180F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        if(ForgeConfigHandler.client.worldHingePoint == ForgeConfigHandler.ClientConfig.HingePoint.BASE) {
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        }

        GlStateManager.translate(0, entityPlayer.height * (baseScale / 0.025F), 0.1);
        GlStateManager.scale(baseScale, baseScale, baseScale);

        GlStateManager.disableRescaleNormal();
        enableWorldOverlayStandardItemLighting();
        if(ForgeConfigHandler.client.worldHingePoint == ForgeConfigHandler.ClientConfig.HingePoint.INFO) {
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.translate(
                -(ForgeConfigHandler.client.xWorldOffset - 8) * distanceScale,
                ForgeConfigHandler.client.yWorldOffset - 4,
                0
        );

        GlStateManager.pushMatrix();
        if(ForgeConfigHandler.client.worldIconsIgnoreDepth) GlStateManager.disableDepth();
        GlStateManager.scale(-distanceScale, -distanceScale, -0.001);

        mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(
                0, 0,
                8, 8,
                8, 8,
                16, 16,
                64, 64
        );
        if (wearingHat) {
            Gui.drawScaledCustomSizeModalRect(
                    0, 0,
                    40, 8,
                    8, 8,
                    16, 16,
                    64, 64
            );
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        GlStateManager.enableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}