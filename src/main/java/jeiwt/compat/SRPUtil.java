package jeiwt.compat;

import com.dhanantry.scapeandrunparasites.item.ItemMobSpawner;
import jeiwt.mixin.srp.ItemMobSpawner_Accessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;

public class SRPUtil {

    public static boolean needLoginLoad = false;

    public static boolean isSpawnEgg(ItemStack stack) {
        return stack.getItem() instanceof ItemMobSpawner;
    }

    // No practical way to map Stack -> Entity besides spawn logic
    public static Class<? extends Entity> getEntityClass(ItemStack stack) {
        Class<? extends Entity> clazz = null;

        if(stack.getItem() instanceof ItemMobSpawner_Accessor) {
            if(Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null) {
                Entity entity = ((ItemMobSpawner_Accessor) stack.getItem()).jeiwt$invokeSpawnEntity(
                        Minecraft.getMinecraft().world,
                        Minecraft.getMinecraft().player.posX,
                        -Minecraft.getMinecraft().world.getHeight(),
                        Minecraft.getMinecraft().player.posZ,
                        Minecraft.getMinecraft().player
                );
                if(entity.getClass() != EntityZombie.class) clazz = entity.getClass();
                needLoginLoad = false;
            }
            else {
                needLoginLoad = true;
            }
        }

//        if(isSpawnEgg(stack)) JEIWantThat.LOGGER.log(Level.INFO, "SRP: {}", clazz);

        return clazz;
    }
}
