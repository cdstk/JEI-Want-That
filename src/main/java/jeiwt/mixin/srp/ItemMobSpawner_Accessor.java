package jeiwt.mixin.srp;

import com.dhanantry.scapeandrunparasites.item.ItemMobSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemMobSpawner.class)
public interface ItemMobSpawner_Accessor {

    @Accessor(value = "name", remap = false)
    String jeiwt$getName();

    @Invoker(value = "spawnEntity", remap = false)
    Entity jeiwt$invokeSpawnEntity(World worldIn, double x, double y, double z, EntityPlayer playerIn);
}
