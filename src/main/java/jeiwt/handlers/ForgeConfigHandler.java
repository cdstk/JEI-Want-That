package jeiwt.handlers;

import jeiwt.JEIWantThat;
import jeiwt.handlers.config.EnchantmentSearchConfig;
import jeiwt.handlers.config.EntitySearchConfig;
import jeiwt.handlers.config.ModCompatibilityConfig;
import jeiwt.handlers.config.TileEntitySearchConfig;
import jeiwt.handlers.config.TooltipLineSearchConfig;
import jeiwt.handlers.config.VillagerSearchConfig;
import jeiwt.util.IBookmarkList_DataMixin;
import jeiwt.util.JEIUtil;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = JEIWantThat.MODID)
public class ForgeConfigHandler {

	@Config.Comment("Mod Compatibility")
	@Config.Name("Mod Compatibility")
	public static final ModCompatibilityConfig modComptability = new ModCompatibilityConfig();

	@Config.Comment("Enchanted Book and Enchanted Item Searching")
	@Config.Name("Enchantment Searching")
	public static final EnchantmentSearchConfig enchantmentSearch = new EnchantmentSearchConfig();

	@Config.Comment("Item Tooltip Searching")
	@Config.Name("Tooltip Searching")
	public static final TooltipLineSearchConfig tooltipLineSearch = new TooltipLineSearchConfig();

	@Config.Comment("Tile Entity Searching")
	@Config.Name("Tile Entity Searching")
	public static final TileEntitySearchConfig tileEntitySearch = new TileEntitySearchConfig();

	@Config.Comment("Entity Searching")
	@Config.Name("Entity Searching")
	public static final EntitySearchConfig entitySearch = new EntitySearchConfig();

	@Config.Comment("Villager Searching")
	@Config.Name("Villager Searching")
	public static final VillagerSearchConfig villagerSearch = new VillagerSearchConfig();

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
//
//		@Config.Name("X Float")
//		public float xFloat = 0;
//
//		@Config.Name("Y Float")
//		public float yFloat = 0;
//	}

	public static class ClientConfig {

		@Config.Comment("JEI Want That Keybinds will toggle functions. Else they will need to be held down.")
		@Config.Name("Handle Keybinds As Toggles")
		public boolean keybindsAsToggles = false;

		@Config.Comment({
				"What to display when there are no simplified tooltips to display." +
						"\tITEM_STACK - Item Icon and Stack size" +
						"\tDISPLAY_NAME - Item Display Name" +
						"\tNONE - A simple bar"
		})
		@Config.Name("Empty Tooltip Render")
		public EmptyTooltipRender emptyTooltipRender = EmptyTooltipRender.ITEM_STACK;
		public enum EmptyTooltipRender {ITEM_STACK, DISPLAY_NAME, NONE }

		@Config.Comment({
				"The \"Modified Tooltip\" Key behavior" +
						"\tHIDE_POLE - Pole is not rendered" +
						"\tMOVE_UP_ALL - Pole and tooltip moved up" +
						"\tMOVE_UP_POLE - Pole shortened"
		})
		@Config.Name("World Modified Tooltip Key Behavior")
		public WorldModifyKeyBehavior worldModifyKey = WorldModifyKeyBehavior.HIDE_POLE;
		public enum WorldModifyKeyBehavior {HIDE_POLE, MOVE_UP_ALL, MOVE_UP_POLE}

		@Config.Comment("X Offset for Tooltips rendered in the World")
		@Config.Name("World X Tooltip Offset")
		public int xWorldOffset = 0;

		@Config.Comment("Y Offset for Tooltips rendered in the World")
		@Config.Name("World Y Tooltip Offset")
		public int yWorldOffset = 28;

		@Config.Comment("Base Scale for the Tooltip Render")
		@Config.Name("World Tooltip Base Scale")
		public float baseScale = 0.025F;

		@Config.Comment({
				"FOV for World Tooltips is calculated using this value added to the game's FOV setting." +
				"Other mods may affect the camera and this helps tweak it." +
				"Lower FOV renders less and higher renders more."
		})
		@Config.Name("World FOV Modifier")
		public int fovModifier = 30;

		@Config.Comment("Minimum Distance before Tooltip Render will be scaled based on distance")
		@Config.Name("World Minimum Distance For Scaling")
		public float distanceScaleStart = 8F;

		@Config.Comment("How much distance affects the size of the Tooltip Render")
		@Config.Name("World Distance Scale Factor")
		public float distanceScaleFactor = 0.2F;

		@Config.Comment("Completely disable the Pole from rendering")
		@Config.Name("World Pole Disable")
		public boolean poleDisable = false;

		@Config.Comment("Scale for the Pole Render Width")
		@Config.Name("World Pole Scale")
		public float poleScale = 1;

		@Config.Comment("Maximum Distance where the mouse can be pointed to bring the closest tooltips to the foreground.")
		@Config.Name("World Mouse Target Range")
		@Config.RangeDouble(max = 128)
		public float mouseTargetRange = 32F;

		@Config.Comment("Whether the mouse can target blocks, else it can only select entities.")
		@Config.Name("World Mouse Target Blocks")
		public boolean mouseTargetBlock = true;

		@Config.Comment({
				"Allows Item Icons to be rendered through blocks, will cause certain items to layer incorrectly." +
				"Incorrect visuals include enchantment glints and 3d models such as chests."
		})
		@Config.Name("World Item Icons Render Through Blocks")
		public boolean worldIconsIgnoreDepth = true;

		@Config.Comment("X Offset for Tooltips rendered in the Inventory")
		@Config.Name("Inventory X Tooltip Offset")
		public int xInventoryOffset = 6;

		@Config.Comment("Y Offset for Tooltips rendered in the Inventory")
		@Config.Name("Inventory Y Tooltip Offset")
		public int yInventoryOffset = 0;

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
				if(JEIUtil.bookmarkList instanceof IBookmarkList_DataMixin) {
					((IBookmarkList_DataMixin) JEIUtil.bookmarkList).jeiwt$initBookmarkedData();
					JEIUtil.initFiltered();
				}
			}
		}
	}
}