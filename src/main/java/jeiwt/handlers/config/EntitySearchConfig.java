package jeiwt.handlers.config;

import net.minecraftforge.common.config.Config;

public class EntitySearchConfig {

    @Config.Comment("Display the location of entities when their Spawn Egg is Bookmarked")
    @Config.Name("Search Spawn Eggs")
    public boolean enabled = true;

    @Config.Comment({
            "Alternates the \"Full Tooltip\" Key behavior",
            "\tON - Holding/Toggle shows Name",
            "\tOFF - Holding/Toggle shows Spawn Egg"
    })
    @Config.Name("World Alternate Full Tooltip Behavior")
    public boolean worldSwapFullBehavior = true;
}
