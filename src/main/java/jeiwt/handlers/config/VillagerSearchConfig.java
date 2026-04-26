package jeiwt.handlers.config;

import net.minecraftforge.common.config.Config;

public class VillagerSearchConfig {

    @Config.Comment("Display the location of Villagers whose profession is specified in the list of \"Professions to Find\"")
    @Config.Name("Search Villager Professions")
    public boolean enabled = true;

    @Config.Comment({
            "Alternates the \"Full Tooltip\" Key behavior",
            "\tON - Holding/Toggle shows Profession Registry ID",
            "\tOFF - Holding/Toggle shows Name"
    })
    @Config.Name("World Alternate Full Tooltip Behavior")
    public boolean worldSwapFullBehavior = false;

    @Config.Comment("Item given to RenderTooltipEvent for styling the Tooltip")
    @Config.Name("RenderTooltipEvent Item")
    public String tooltipItemStyle = "minecraft:record_cat , 0";

    @Config.Comment({
            "Villager Profession Registry Names to search for, supports wildcards.",
            "\tminecraft:* - All vanilla",
            "\t* - All registered",
            "Note: Some mods do not register Professions and must be fully specified."
    })
    @Config.Name("Professions to Find")
    public String[] villagerProfessions = {
            "minecraft:librarian",
            "iceandfire:fisherman",
            "iceandfire:craftsman",
            "iceandfire:shaman"
    };
}
