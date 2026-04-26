package jeiwt.compat;

import com.dhanantry.scapeandrunparasites.item.ItemMobSpawner;
import jeiwt.JEIWantThat;
import jeiwt.mixin.srp.ItemMobSpawner_Accessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

public class SRPUtil {

    public static boolean needLoginLoad = false;

//    private static final Map<String, String> nameToID;
//    static {
//        Map<String, String> initMap = new HashMap<>();
//        initMap.put("pod", "anc_pod");
//        initMap.put("mes", "thrall");
//        initMap.put("cruxa", "crux");
//        initMap.put("heed", "heed");
//        initMap.put("jinjo", "bomber_heavy");
//        initMap.put("elvia", "wraith");
//        initMap.put("pheon", "haunter");
//        initMap.put("lencia", "ballmall");
//        initMap.put("vesta", "carrier_colony");
//        initMap.put("shyco", "pri_longarms");
//        initMap.put("shycoadapted", "ada_longarms");
//        initMap.put("dorpa", "sim_bigspider");
//        initMap.put("rathol", "carrier_heavy");
//        initMap.put("gothol", "carrier_light");
//        initMap.put("emana", "pri_yelloweye");
//        initMap.put("emanaadapted", "ada_yelloweye");
//        initMap.put("iki", "pri_vermin");
//        nameToID = Collections.unmodifiableMap(initMap);
//    }

    public static boolean isSpawnEgg(ItemStack stack) {
        return stack.getItem() instanceof ItemMobSpawner;
    }

//    public static ResourceLocation getEntityID(ItemStack stack) {
//        ResourceLocation entityID = null;
//
//        if(stack.getItem() instanceof ItemMobSpawner_Accessor) {
//            String name = ((ItemMobSpawner_Accessor) stack.getItem()).jeiwt$getName();
//            if(nameToID.containsKey(name)) {
//                entityID = new ResourceLocation(SRPReference.MOD_ID, nameToID.get(name));
//            }
//        }
//
//        return entityID;
//    }

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

        if(isSpawnEgg(stack)) JEIWantThat.LOGGER.log(Level.INFO, "SRP: {}", clazz);

        return clazz;
    }
}
