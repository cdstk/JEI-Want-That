package jeiwt.handlers.config;

import net.minecraftforge.common.config.Config;

public class InventoryTooltipConfig {

    @Config.Comment({
            "Which Tooltip to render with the \"Modified Tooltip\" or \"Full Tooltip\" Key.",
            "\tVERTICAL - Items in the same column as the hovered slot",
            "\tHORIZONTAL - Items in the same row as the hovered slot",
            "\tALL - All Items"
    })
    @Config.Name("Key Bind Behavior")
    public TooltipSelection keyBindBehavior = TooltipSelection.VERTICAL;
    public enum TooltipSelection {VERTICAL, HORIZONTAL, ALL }

    @Config.Comment("X Offset for Tooltips rendered in the Inventory")
    @Config.Name("Tooltip X Offset")
    public int xOffset = 0;

    @Config.Comment("Y Offset for Tooltips rendered in the Inventory")
    @Config.Name("Tooltip Y Offset")
    public int yOffset = 8;

    @Config.Comment("If true, the tooltip is rendered on the left side of the item")
    @Config.Name("Tooltip Position Left Side")
    public boolean posLeftSide = false;

    @Config.Comment("The rotation of the tooltip in degrees")
    @Config.Name("Tooltip Rotation Angle")
    public float rotationAngle = 0;

    @Config.Comment("Tooltips on the left side will be layered on top of right side ones")
    @Config.Name("Display Left Over Right")
    public boolean leftOverRight = false;

    @Config.Comment("Tooltips on the top side will be layered on top of bottom side ones")
    @Config.Name("Display Top Over Bottom")
    public boolean topOverBottom = false;

    @Config.Comment({
            "When hovering over a slot, items on the specified side will not display tooltips",
            "\tNONE - Does not hide any"
    })
    @Config.Name("Hide Horizontal")
    public HorizontalOption hideHorizontal = HorizontalOption.NONE;
    public enum HorizontalOption {LEFT, RIGHT, NONE}


    @Config.Comment({
            "When hovering over a slot, items on the specified side will not display tooltips",
            "\tNONE - Does not hide any"
    })
    @Config.Name("Hide Vertical")
    public VerticalOption hideVertical = VerticalOption.NONE;
    public enum VerticalOption {TOP, BOTTOM, NONE}
}
