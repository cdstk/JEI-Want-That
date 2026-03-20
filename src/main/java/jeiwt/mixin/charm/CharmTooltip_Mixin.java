package jeiwt.mixin.charm;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import jeiwt.compat.CharmUtil;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charm.crafting.feature.Crate;

@Mixin(value = {
        Crate.class
})
public class CharmTooltip_Mixin {

    @WrapMethod(
            method = "makeTooltip",
            remap = false
    )
    private void jeiwt_charmTooltipFeatures_makeTooltip(ItemTooltipEvent event, Operation<Void> original){
        if(CharmUtil.doCharmRender()) original.call(event);
    }

    @WrapMethod(
            method = "renderTooltip",
            remap = false
    )
    private void jeiwt_charmTooltipFeatures_renderTooltip(RenderTooltipEvent.PostText event, Operation<Void> original){
        if(CharmUtil.doCharmRender()) original.call(event);
    }
}
