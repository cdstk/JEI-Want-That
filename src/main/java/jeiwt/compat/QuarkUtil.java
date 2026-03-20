package jeiwt.compat;

import jeiwt.handlers.ForgeConfigHandler;
import vazkii.quark.client.feature.ChestSearchBar;

public class QuarkUtil {

    public static boolean doQuarkRender(){
        return !ChestSearchBar.skip;
    }

    public static void setSkipQuarkRender(){
        if(ForgeConfigHandler.modComptability.quarkDisableTooltip) {
            ChestSearchBar.skip = true;
        }
    }

    public static void resetSkipQuarkRender(){
        if(ForgeConfigHandler.modComptability.quarkDisableTooltip) {
            ChestSearchBar.skip = false;
        }
    }
}
