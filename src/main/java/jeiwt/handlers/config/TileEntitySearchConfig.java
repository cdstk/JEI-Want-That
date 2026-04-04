package jeiwt.handlers.config;

import net.minecraftforge.common.config.Config;

public class TileEntitySearchConfig {

    @Config.Comment("If a bookmarked item is also a Tile Entity, mark them in the World Tooltip Overlay")
    @Config.Name("Search Tile Entities")
    public boolean enabled = true;

    @Config.Comment("Shulker Boxes will be marked regardless of line of sight")
    @Config.Name("Always Show Shulker Boxes")
    public boolean shulkerBoxes = true;

    @Config.Comment("Crates will be marked regardless of line of sight")
    @Config.Name("Always Show Crates (Charm)")
    public boolean charmCrates = true;

    @Config.Comment("Backpacks will be marked regardless of line of sight")
    @Config.Name("Always Show Backpacks (Wearable Backpacks)")
    public boolean wearableBackpacks = true;

    @Config.Comment("Natural Waystones will be marked regardless of line of sight")
    @Config.Name("Always Show Natural Waystones (Waystones)")
    public boolean waystonesNatural = true;

    @Config.Comment("Discovered Waystones will be marked regardless of line of sight")
    @Config.Name("Always Show Known Waystones (Waystones)")
    public boolean waystonesKnown = true;
}
