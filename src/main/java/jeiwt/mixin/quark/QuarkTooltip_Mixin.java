package jeiwt.mixin.quark;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import jeiwt.compat.QuarkUtil;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.quark.client.feature.EnchantedBooksShowItems;
import vazkii.quark.client.feature.FoodTooltip;
import vazkii.quark.client.feature.MapTooltip;
import vazkii.quark.client.feature.ShulkerBoxTooltip;
import vazkii.quark.client.feature.VisualStatDisplay;

@Mixin(value = {
        EnchantedBooksShowItems.class,
        FoodTooltip.class,
        MapTooltip.class,
        ShulkerBoxTooltip.class,
        VisualStatDisplay.class
})
public class QuarkTooltip_Mixin {

    @WrapMethod(
            method = "makeTooltip",
            remap = false
    )
    private void jeiwt_quarkTooltipFeatures_makeTooltip(ItemTooltipEvent event, Operation<Void> original){
        if(QuarkUtil.doQuarkRender()) original.call(event);
    }

    @WrapMethod(
            method = "renderTooltip",
            remap = false
    )
    private void jeiwt_quarkTooltipFeatures_renderTooltip(RenderTooltipEvent.PostText event, Operation<Void> original){
        if(QuarkUtil.doQuarkRender()) original.call(event);
    }
}
