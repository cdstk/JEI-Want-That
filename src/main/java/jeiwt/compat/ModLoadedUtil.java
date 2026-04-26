package jeiwt.compat;

import net.minecraftforge.fml.common.Loader;

// Nischhelm Style
public class ModLoadedUtil {

    // Still awaits Fermium Booter CF update to check versions for mixins
    public static final String JEI_MODID = "jei";
    public static final String CHARM_MODID = "charm";
    public static final String LYCANITES_MODID = "lycanitesmobs";
    public static final String QUARK_MODID = "quark";
    public static final String SRP_MODID = "srparasites";
    public static final String WEARABLE_BACKPACKS_MODID = "wearablebackpacks";
    public static final String WAYSTONES_MODID = "waystones";

    public static final LoadedContainer CHARM = new LoadedContainer(CHARM_MODID);
    public static final LoadedContainer LYCANITES = new LoadedContainer(LYCANITES_MODID);
    public static final LoadedContainer QUARK = new LoadedContainer(QUARK_MODID);
    public static final LoadedContainer SRP = new LoadedContainer(SRP_MODID);
    public static final LoadedContainer WEARABLE_BACKPACKS = new LoadedContainer(WEARABLE_BACKPACKS_MODID);
    public static final LoadedContainer WAYSTONES = new LoadedContainer(WAYSTONES_MODID);

    public static final LoadedContainer HAD_ENOUGH_ITEMS = new LoadedContainer(JEI_MODID){
        @Override
        public boolean isLoaded(){
            if(this.isLoaded == null){
                this.isLoaded = false;
                String[] arrOfStr = Loader.instance().getIndexedModList().get(JEI_MODID).getVersion().split("\\.");
                try {
                    if (Integer.parseInt(String.valueOf(arrOfStr[1])) > 28) {
                        this.isLoaded = true;
                    }
                }
                catch (Exception ignored) {}
            }
            return this.isLoaded;
        }
    };

    public static class LoadedContainer {
        protected Boolean isLoaded = null;
        private final String key;
        private LoadedContainer(String key){
            this.key = key;
        }
        public boolean isLoaded(){
            if(this.isLoaded == null) isLoaded = Loader.isModLoaded(key);
            return isLoaded;
        }
    }
}
