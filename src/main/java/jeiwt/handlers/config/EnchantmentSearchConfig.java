package jeiwt.handlers.config;

import net.minecraftforge.common.config.Config;

public class EnchantmentSearchConfig {

    @Config.Comment("If an item has Applied Enchantments, display the enchantments")
    @Config.Name("Search Applied on Items")
    public boolean enabled = false;

    @Config.Comment("If an item has Applied Enchantments, display only the top one")
    @Config.Name("Search Applied Top Only")
    public boolean matchTopOnly = false;

    @Config.Comment("All Enchantment Books will be shown")
    @Config.Name("Match Any Book")
    public boolean allBooks = false;

    @Config.Comment("Item IDs for JEI Bookmarked items. These items have all their Applied Enchantments added to the search.")
    @Config.Name("Bookmarked Item IDs")
    public String[] bookmarkedItemIDs = { "" };
}
