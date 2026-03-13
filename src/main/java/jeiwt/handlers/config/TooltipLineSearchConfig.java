package jeiwt.handlers.config;

import net.minecraftforge.common.config.Config;

public class TooltipLineSearchConfig {

    @Config.Comment("Lang Keys to check for and display from Tooltips. Does not support any lang key with one or more parameters.")
    @Config.Name("Lang Keys")
    public String[] langKeys = {
            "item.unbreakable"
    };

    @Config.Comment("Regular Expression Patterns to check and display from Tooltips")
    @Config.Name("Regex Patterns")
    public String[] regexPatterns = {
            ".*Sockets.*"
    };
}
