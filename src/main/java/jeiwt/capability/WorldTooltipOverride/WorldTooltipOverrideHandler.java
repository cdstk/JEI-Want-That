package jeiwt.capability.WorldTooltipOverride;

import jeiwt.JEIWantThat;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldTooltipOverrideHandler {

    public static final ResourceLocation WORLD_TOOLTIP_KEY = new ResourceLocation(JEIWantThat.MODID, "worldtooltip");

    @CapabilityInject(WorldTooltipOverride.class)
    public static Capability<WorldTooltipOverride> WORLD_TOOLTIP;

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(WorldTooltipOverride.class, new Storage(), WorldTooltipOverride::new);
    }

    public static class AttachCapabilityHandler {
        @SubscribeEvent()
        public static void onAttachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
            Entity entity = event.getObject();

            if(entity.hasCapability(WORLD_TOOLTIP, null)) return;

            event.addCapability(WORLD_TOOLTIP_KEY, new Provider());
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private final WorldTooltipOverride instance;

        public Provider() {
            this.instance = new WorldTooltipOverride();
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == WORLD_TOOLTIP;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == WORLD_TOOLTIP ? WORLD_TOOLTIP.cast(instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) WORLD_TOOLTIP.writeNBT(instance, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            WORLD_TOOLTIP.readNBT(instance, null, nbt);
        }
    }

    private static class Storage implements Capability.IStorage<WorldTooltipOverride> {

        @Override
        public NBTBase writeNBT(Capability<WorldTooltipOverride> capability, WorldTooltipOverride instance, EnumFacing side) {
            return new NBTTagCompound();
        }

        @Override
        public void readNBT(Capability<WorldTooltipOverride> capability, WorldTooltipOverride instance, EnumFacing side, NBTBase nbt) {

        }
    }
}
