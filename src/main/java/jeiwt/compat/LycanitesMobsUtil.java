package jeiwt.compat;

import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LycanitesMobsUtil {

    public static boolean isSpawnEgg(ItemStack stack) {
        return stack.getItem() instanceof ItemCustomSpawnEgg;
    }

    public static ResourceLocation getEntityID(ItemStack stack) {
        ResourceLocation entityID = null;

        if(stack.getItem() instanceof ItemCustomSpawnEgg) {
            CreatureInfo creatureInfo = ((ItemCustomSpawnEgg) stack.getItem()).getCreatureInfo(stack);
            entityID = new ResourceLocation(creatureInfo.getEntityId());
        }

        return entityID;
    }
}
