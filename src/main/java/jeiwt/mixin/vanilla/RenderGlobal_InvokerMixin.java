package jeiwt.mixin.vanilla;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderGlobal.class)
public interface RenderGlobal_InvokerMixin {

    @Invoker(value = "getViewVector")
    Vector3f invokeGetViewVector(Entity entityIn, double partialTicks);
}
