package jeiwt.handlers.config;

import jeiwt.handlers.ForgeConfigHandler;
import net.minecraftforge.common.config.Config;

public class AntiqueAtlasAutoMarkerConfig {

    @Config.Comment({
            "Display the list of enchantments traded from the nearest valid Marker",
            "All valid Markers are best guesses and may not be related to the Villager at all.",
            "Render options based on main \"World Tooltip\" config"
    })
    @Config.Name("Villager Enchantments")
    public boolean villagerMarkers = true;

    @Config.Comment({
            "Display any markers with the type specified in AAAM's \"Enchantments\" Config",
            "Render options are here in with the various \"Marker\" configs"
    })
    @Config.Name("Villager Enchantments - Display Valid Markers")
    public boolean enchantmentMarkers = true;

    @Config.Comment({
            "Alternates the \"Full Tooltip\" Key behavior",
            "\tON - Holding/Toggle shows Marker Icon + Label",
            "\tOFF - Holding/Toggle only shows Marker Icon"
    })
    @Config.Name("World Markers Alternate Full Tooltip Behavior")
    public boolean markersSwapFullBehavior = false;

    @Config.Comment({
            "The maximum distance from the Villager that a Marker can be.",
            "Markers may not be at Villager coordinates."
    })
    @Config.Name("Villager Enchantments - Distance From Marker")
    public float villagerDistance = 5.0F;

    @Config.Comment({
            "If line of sight is considered for valid Markers.",
            "Valid Markers will be the closest one within Line of Sight",
            "If none are found, the closest Marker is considered Valid"
    })
    @Config.Name("Villager Enchantments - Line Of Sight")
    public boolean villagerLineOfSight = false;

    @Config.Comment("Base Height of the pole. In most other renders, it is the height of the entity.")
    @Config.Name("Marker Pole Height")
    public float markerPoleHeight = -1;

    @Config.Comment("The Player's eye height will be added to the base height")
    @Config.Name("Marker Pole Height - Player Eye Height")
    public boolean markerPoleHeightEye = true;

    @Config.Comment("X Offset for Tooltips rendered in the World")
    @Config.Name("Marker Icon Scale")
    public float markerIconScale = 1.5F;

    @Config.Comment("X Offset for Tooltips rendered in the World")
    @Config.Name("Marker Icon X Offset")
    public int xMarkerIconOffset = 12;

    @Config.Comment("Y Offset for Tooltips rendered in the World")
    @Config.Name("Marker Icon Y Offset")
    public int yMarkerIconOffset = 9;

    @Config.Comment("The point where the tooltip will bend to \"face\" the player.")
    @Config.Name("World Marker Hinge Point")
    public ForgeConfigHandler.ClientConfig.HingePoint worldHingePoint = ForgeConfigHandler.ClientConfig.HingePoint.INFO;
}
