package jeiwt.handlers.config;

import net.minecraftforge.common.config.Config;

public class PlayerSearchConfig {

    @Config.Comment("Display the location of other nearby players, similar to name tags, respects sneaking and player teams")
    @Config.Name("Search Players")
    public boolean enabled = true;

    @Config.Comment("Item given to RenderTooltipEvent for styling the Tooltip")
    @Config.Name("RenderTooltipEvent Item")
    public String tooltipItemStyle = "minecraft:beacon , 0";
}
