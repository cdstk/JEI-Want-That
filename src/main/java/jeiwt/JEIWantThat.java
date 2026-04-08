package jeiwt;

import jeiwt.client.handlers.KeyHandler;
import jeiwt.client.renderer.InventoryHighlightRenderer;
import jeiwt.client.renderer.WorldTooltipRenderer;
import jeiwt.compat.CharmUtil;
import jeiwt.compat.ModLoadedUtil;
import jeiwt.compat.QuarkUtil;
import jeiwt.handlers.ForgeConfigProvider;
import jeiwt.util.IBookmarkList_DataMixin;
import jeiwt.util.JEIUtil;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = JEIWantThat.MODID,
        version = JEIWantThat.VERSION,
        name = JEIWantThat.NAME,
        clientSideOnly = true,
        dependencies =
                "required-after:fermiumbooter;" +
                "required-after:jei;"
)
public class JEIWantThat {
    public static final String MODID = "jeiwt";
    public static final String VERSION = "1.0.2.1";
    public static final String NAME = "JEI Want That";
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean completedLoading = false;
	
	@Instance(MODID)
	public static JEIWantThat instance;
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Client
        KeyHandler.initKeybind();
        MinecraftForge.EVENT_BUS.register(InventoryHighlightRenderer.class);
        MinecraftForge.EVENT_BUS.register(WorldTooltipRenderer.class);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ForgeConfigProvider.init();

        // Had Enough Items
        if(JEIUtil.bookmarkList instanceof IBookmarkList_DataMixin) {
            ((IBookmarkList_DataMixin) JEIUtil.bookmarkList).jeiwt$initBookmarkedData();
        }
        completedLoading = true;
    }

    // Client
    public static boolean checkLangKey(String langKey, String checkString){
        return I18n.hasKey(langKey) && checkString.contains(I18n.format(langKey));
    }

    public static void setSkipModdedTooltips(){
        if (ModLoadedUtil.CHARM.isLoaded()) CharmUtil.setSkipCharmRender();
        if (ModLoadedUtil.QUARK.isLoaded()) QuarkUtil.setSkipQuarkRender();
    }

    public static void resetSkipModdedTooltips(){
        if (ModLoadedUtil.CHARM.isLoaded()) CharmUtil.resetSkipCharmRender();
        if (ModLoadedUtil.QUARK.isLoaded()) QuarkUtil.resetSkipQuarkRender();
    }
}