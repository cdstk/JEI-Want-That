package jeiwt.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import jeiwt.client.renderer.InventoryHighlightRenderer;
import jeiwt.handlers.ForgeConfigHandler;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiUtils.class)
public abstract class GuiUtils_TooltipBackgroundMixin {

    @ModifyExpressionValue(
            method = "drawHoveringText(Lnet/minecraft/item/ItemStack;Ljava/util/List;IIIIILnet/minecraft/client/gui/FontRenderer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/RenderTooltipEvent$Color;getBackground()I"),
            remap = false
    )
    private static int jeiwt_forgeGuiUtils_drawHoveringTextHighlightTransparent(int backgroundColor){
        if(InventoryHighlightRenderer.isRenderingStackBackground() && ForgeConfigHandler.client.backgroundTransparent) {
            return 0;
        }
        return backgroundColor;
    }
}