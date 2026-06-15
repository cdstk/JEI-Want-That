package jeiwt.mixin.vanilla;

import jeiwt.JEIWantThat;
import jeiwt.capability.WorldTooltipOverride.IWorldTooltipOverride;
import jeiwt.capability.WorldTooltipOverride.WorldTooltipOverrideHandler;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMerchant.class)
public abstract class GuiMerchant_CareerMixin {

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void jeiwt_vanillaGuiMerchant_initGetVillagerCareer(InventoryPlayer inventoryPlayer, IMerchant merchant, World world, CallbackInfo ci){
        EntityVillager villager = JEIWantThat.getLastClickedVillager();
        if(villager != null) {
            IWorldTooltipOverride tooltipOverride = villager.getCapability(WorldTooltipOverrideHandler.WORLD_TOOLTIP, null);
            if(tooltipOverride != null) {
                tooltipOverride.setDisplayName(merchant.getDisplayName().getFormattedText());
            }
        }
    }
}
