package jeiwt.handlers.config;

import fermiumbooter.annotations.MixinConfig;
import jeiwt.JEIWantThat;
import jeiwt.compat.ModLoadedUtil;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = JEIWantThat.MODID)
public class ModCompatibilityConfig {

    @Config.Comment("Antique Atlas Auto Marker")
    @Config.Name("Antique Atlas Auto Marker")
    public final AntiqueAtlasAutoMarkerConfig aaam = new AntiqueAtlasAutoMarkerConfig();

    @Config.Comment("Prevent Charm Crate contents tooltip from being rendered by JEI Want That")
    @Config.Name("Disable Modded Tooltips (Charm)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.jeiwt.charm.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.CHARM_MODID,
            desired = true,
            warnIngame = false
    )
    public boolean charmDisableTooltip = true;

    @Config.Comment("Prevent Quark tooltip features from being rendered by JEI Want That")
    @Config.Name("Disable Modded Tooltips (Quark)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.jeiwt.quark.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.QUARK_MODID,
            desired = true,
            warnIngame = false
    )
    public boolean quarkDisableTooltip = true;
}
