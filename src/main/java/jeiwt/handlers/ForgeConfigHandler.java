package jeiwt.handlers;

import jeiwt.handlers.config.EnchantmentSearchConfig;
import jeiwt.handlers.config.TooltipLineSearchConfig;
import jeiwt.util.IBookmarkList_DataMixin;
import jeiwt.util.JEIUtil;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import jeiwt.JEIWantThat;

@Config(modid = JEIWantThat.MODID)
public class ForgeConfigHandler {

	@Config.Comment("Enchanted Book and Enchanted Item Searching")
	@Config.Name("Enchantment Searching")
	public static final EnchantmentSearchConfig enchantmentSearch = new EnchantmentSearchConfig();

	@Config.Comment("Item Tooltip Searching")
	@Config.Name("Tooltip Searching")
	public static final TooltipLineSearchConfig tooltipLineSearch = new TooltipLineSearchConfig();

	@Config.Comment("Client-Side Options")
	@Config.Name("Client Options")
	public static final ClientConfig client = new ClientConfig();

//	@Config.Name("Debug Options")
//	public static final DebugConfig debug = new DebugConfig();
//
//	public static class DebugConfig {
//
//		@Config.Name("String Array")
//		public String[] strArray = { " ", " " };
//
//		@Config.Name("X Int")
//		public int xInt = 0;
//
//		@Config.Name("Y Int")
//		public int yInt = 0;
//	}

	public static class ClientConfig {

		@Config.Comment({
				"What to display when there are no simplified tooltips to display." +
						"\tITEM_STACK - Item Icon and Stack size" +
						"\tDISPLAY_NAME - Item Display Name" +
						"\tNONE - A simple bar"
		})
		@Config.Name("Empty Tooltip Render")
		public EmptyTooltipRender emptyTooltipRender = EmptyTooltipRender.ITEM_STACK;
		public enum EmptyTooltipRender {ITEM_STACK, DISPLAY_NAME, NONE }

		@Config.Comment("X Offset for Tooltips rendered in the World")
		@Config.Name("World X Tooltip Offset")
		public int xWorldOffset = 0;

		@Config.Comment("Y Offset for Tooltips rendered in the World")
		@Config.Name("World Y Tooltip Offset")
		public int yWorldOffset = 28;

		@Config.Comment("Base Scale for the Tooltip Render")
		@Config.Name("World Tooltip Base Scale")
		public float baseScale = 0.025F;

		@Config.Comment("Minimum Distance before Tooltip Render will be scaled based on distance")
		@Config.Name("World Minimum Distance For Scaling")
		public float distanceScaleStart = 8F;

		@Config.Comment("How much distance affects the size of the Tooltip Render")
		@Config.Name("World Distance Scale Factor")
		public float distanceScaleFactor = 0.2F;

		@Config.Comment("Maximum Distance where the mouse can be pointed to bring the closest tooltips to the foreground.")
		@Config.Name("World Mouse Target Range")
		public float mouseTargetRange = 32F;

		@Config.Comment("X Offset for Tooltips rendered in the Inventory")
		@Config.Name("Inventory X Tooltip Offset")
		public int xInventoryOffset = 6;

		@Config.Comment("Y Offset for Tooltips rendered in the Inventory")
		@Config.Name("Inventory Y Tooltip Offset")
		public int yInventoryOffset = 16;

		@Config.Comment("Non-Desired Items will be darkened in Inventory with the \"Modified Tooltip\" Key")
		@Config.Name("Inventory Darken Non-Desirable")
		public boolean darkenNonDesirable = true;

		@Config.Comment("Desired items Items be overlay a contrasting background in Inventory with the \"Modified Tooltip\" Key")
		@Config.Name("Inventory Background for Desirable")
		public boolean backgroundForDesirable = true;

		@Config.Comment({
				"Which Tooltip to render with the \"Modified Tooltip\" Key." +
						"\tVERTICAL - Items in the same column as the hovered slot" +
						"\tHORIZONTAL - Items in the same row as the hovered slot" +
						"\tALL - All Items"
		})
		@Config.Name("Inventory Key Bind Behavior")
		public InventoryShiftRender inventoryShiftRender = InventoryShiftRender.VERTICAL;
		public enum InventoryShiftRender {VERTICAL, HORIZONTAL, ALL }
	}

	@Mod.EventBusSubscriber(modid = JEIWantThat.MODID)
	private static class EventHandler{

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(JEIWantThat.MODID)) {
				ConfigManager.sync(JEIWantThat.MODID, Config.Type.INSTANCE);

				ForgeConfigProvider.init();
				if(JEIUtil.BOOKMARK_LIST instanceof IBookmarkList_DataMixin) {
					((IBookmarkList_DataMixin) JEIUtil.BOOKMARK_LIST).jeiwt$initBookmarkedData();
				}
			}
		}
	}
}