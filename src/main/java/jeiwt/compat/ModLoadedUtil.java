package jeiwt.compat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

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
    public static final String AAAM_MODID = "antiqueatlasautomarker";

    public static final LoadedContainer CHARM = new LoadedContainer(CHARM_MODID);
    public static final LoadedContainer LYCANITES = new LoadedContainer(LYCANITES_MODID);
    public static final LoadedContainer QUARK = new LoadedContainer(QUARK_MODID);
    public static final LoadedContainer SRP = new LoadedContainer(SRP_MODID);
    public static final LoadedContainer WEARABLE_BACKPACKS = new LoadedContainer(WEARABLE_BACKPACKS_MODID);
    public static final LoadedContainer WAYSTONES = new LoadedContainer(WAYSTONES_MODID);
    public static final LoadedContainer AAAM = new LoadedContainer(AAAM_MODID);

    public static final LoadedContainer JEI = new LoadedContainer(JEI_MODID);
    public static final String HEI_VERSION = "[4.29,)";

    // Nischhelm style
    public static boolean versionInRange(LoadedContainer container, String version) {
        if (!container.isLoaded()) return false;
        VersionRange range;
        try {
            range = VersionRange.createFromVersionSpec(version);
        } catch (Exception e) {
            return false;
        }
        return range.containsVersion(container.getVersion());
    }

    public static class LoadedContainer{
        private Boolean isLoaded = null;
        private DefaultArtifactVersion version;
        private final String key;
        private LoadedContainer(String key){
            this.key = key;
        }
        public boolean isLoaded(){
            if(this.isLoaded == null) isLoaded = Loader.isModLoaded(key);
            return isLoaded;
        }
        public DefaultArtifactVersion getVersion(){
            if(version == null) version = new DefaultArtifactVersion(Loader.instance().getIndexedModList().get(key).getVersion());
            return version;
        }
    }
}
