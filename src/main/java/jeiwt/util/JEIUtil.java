package jeiwt.util;

import jeiwt.client.handlers.KeyHandler;
import jeiwt.compat.CharmUtil;
import jeiwt.compat.ModLoadedUtil;
import jeiwt.handlers.ForgeConfigHandler;
import jeiwt.handlers.ForgeConfigProvider;
import mezz.jei.Internal;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JEIUtil {

    public static BookmarkList bookmarkList = null;
    private static String lastFilterText = ""; // I would do a listener if it was actually implemented

    private static final Map<Item, Set<Integer>> FILTERED_ITEMS = new HashMap<>();
    private static final Set<Integer> FILTERED_ENCHANTMENTS = new HashSet<>();
    private static final Map<Item, Set<Integer>> BOOKMARKED_ITEMS = new HashMap<>();
    private static final Set<Integer> BOOKMARKED_ENCHANTMENTS = new HashSet<>();
    private static final Set<Integer> ENCHANTMENTS_FROM_ITEMS = new HashSet<>();

    public static boolean isItemStackDesirable(ItemStack stack){
        return isItemStackDesirable(stack, true);
    }

    public static boolean isItemStackDesirable(ItemStack stack, boolean tooltipCheck){
        if(stack.isEmpty()){
            return false;
        }

        if(!Config.getFilterText().equals(lastFilterText)) initFiltered();

        // Nested Container
        if(checkNestedContainer(stack)){
            return true;
        }

        // Applied Enchantments
        if(stackHasBookmarkedEnchantments(stack)){
            return true;
        }
        
        // Stored Enchantments
        if(stackStoresBookmarkedEnchantments(stack)){
            return true;
        }

        // Config Regex
        if(tooltipCheck && stackMatchesAnyQuery(stack)){
            return true;
        }

        // Bookmarks
        return stackIsBookmarked(stack);
    }

    public static boolean checkNestedContainer(ItemStack stack){
        if(stack.getItem() instanceof ItemShulkerBox && stack.hasTagCompound()) {
            if(stack.getTagCompound().hasKey("BlockEntityTag")){
                NBTTagCompound tagCompound = stack.getTagCompound().getCompoundTag("BlockEntityTag");
                if(tagCompound.hasKey("Items", 9)) {
                    NonNullList<ItemStack> itemList = NonNullList.withSize(27, ItemStack.EMPTY);
                    ItemStackHelper.loadAllItems(tagCompound, itemList);

                    for(ItemStack innerStack : itemList){
                        if(isItemStackDesirable(innerStack)){
                            return true;
                        }
                    }
                }
            }
        }
        if(ModLoadedUtil.CHARM.isLoaded() && CharmUtil.checkNestedCrate(stack)) return true;

        return false;
    }

    public static boolean stackStoresBookmarkedEnchantments(ItemStack stack){
        NBTTagList enchantmentsNBT = ItemEnchantedBook.getEnchantments(stack);
        if(!enchantmentsNBT.isEmpty()){
            if(ForgeConfigHandler.enchantmentSearch.allBooks) return true;
            for (int i = 0; i < enchantmentsNBT.tagCount(); ++i) {
                NBTTagCompound nbtTagCompound = enchantmentsNBT.getCompoundTagAt(i);
                int id = nbtTagCompound.getShort("id");
                if(BOOKMARKED_ENCHANTMENTS.contains(id)) return true;
                if(ENCHANTMENTS_FROM_ITEMS.contains(id)) return true;
                if(FILTERED_ENCHANTMENTS.contains(id)) return true;
            }
        }
        return false;
    }

    public static boolean stackHasBookmarkedEnchantments(ItemStack stack){
        if(!stack.isItemEnchanted()) return false;
        if(!ForgeConfigHandler.enchantmentSearch.enabled) return false;

        NBTTagList enchantmentsNBT = stack.getEnchantmentTagList();
        if(!enchantmentsNBT.isEmpty()){
            for (int i = 0; i < (ForgeConfigHandler.enchantmentSearch.matchTopOnly ? 1 : enchantmentsNBT.tagCount()); ++i) {
                NBTTagCompound nbttagcompound = enchantmentsNBT.getCompoundTagAt(i);
                int id = nbttagcompound.getShort("id");
                if(BOOKMARKED_ENCHANTMENTS.contains(id)) return true;
                if(ENCHANTMENTS_FROM_ITEMS.contains(id)) return true;
                if(FILTERED_ENCHANTMENTS.contains(id)) return true;
            }
        }
        return false;
    }

    public static boolean stackMatchesAnyQuery(ItemStack stack){
        Minecraft mc = Minecraft.getMinecraft();
        List<String> tooltip = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        for(String line : tooltip){
            if(ForgeConfigProvider.checkLineForLangKeys(line)) return true;
            if(ForgeConfigProvider.checkLineForPatterns(line)) return true;
        }
        return false;
    }

    public static boolean stackIsBookmarked(ItemStack stack){
        boolean match = false;
        Set<Integer> metaDatas = BOOKMARKED_ITEMS.get(stack.getItem());
        if(metaDatas != null) match = metaDatas.contains(stack.getMetadata());

        if(!match) {
            metaDatas = FILTERED_ITEMS.get(stack.getItem());
            if(metaDatas != null) match = metaDatas.contains(stack.getMetadata());
        }

        return match;
    }

    public static void initDesirables(){
        BOOKMARKED_ITEMS.clear();
        BOOKMARKED_ENCHANTMENTS.clear();
        ENCHANTMENTS_FROM_ITEMS.clear();
    }

    public static void initFiltered(){
        FILTERED_ITEMS.clear();
        FILTERED_ENCHANTMENTS.clear();
        lastFilterText = Config.getFilterText();

        if(lastFilterText.isEmpty()) return;
        if(!ForgeConfigHandler.tooltipLineSearch.jeiFilteredSearch) return;

        Internal.getIngredientFilter().getFilteredIngredients().forEach(ingredient -> {
            if (ingredient instanceof EnchantmentData) {
                FILTERED_ENCHANTMENTS.add(Enchantment.getEnchantmentID(((EnchantmentData) ingredient).enchantment));
            } else if (ingredient instanceof ItemStack) {
                ItemStack itemStack = (ItemStack) ingredient;
                Set<Integer> metaDatas = FILTERED_ITEMS.computeIfAbsent(itemStack.getItem(), mapStack -> new HashSet<>());
                metaDatas.add(itemStack.getMetadata());
            }
        });
    }

    public static void modifyBookmarkedItems(ItemStack stack, int metadata) { modifyBookmarkedItems(stack, metadata, true); }
    public static void modifyBookmarkedItems(ItemStack stack, int metadata, boolean add){
        if(stack.getItem() == Items.ENCHANTED_BOOK) return;

        if(add){
            Set<Integer> metaDatas = BOOKMARKED_ITEMS.computeIfAbsent(stack.getItem(), mapStack -> new HashSet<>());
            metaDatas.add(metadata);
        }
        else {
            Set<Integer> metaDatas = BOOKMARKED_ITEMS.get(stack.getItem());
            if(metaDatas != null) metaDatas.remove(metadata);
        }
    }

    public static void modifyBookmarkedEnchantments(int enchantmentID) { modifyBookmarkedEnchantments(enchantmentID, true); }
    public static void modifyBookmarkedEnchantments(int enchantmentID, boolean add){
        if(add){
            BOOKMARKED_ENCHANTMENTS.add(enchantmentID);
        }
        else {
            BOOKMARKED_ENCHANTMENTS.remove(enchantmentID);
        }
    }

    public static void modifyEnchantmentsFromItems(int enchantmentID) { modifyEnchantmentsFromItems(enchantmentID, true); }
    public static void modifyEnchantmentsFromItems(int enchantmentID, boolean add){
        if(add){
            ENCHANTMENTS_FROM_ITEMS.add(enchantmentID);
        }
        else {
            ENCHANTMENTS_FROM_ITEMS.remove(enchantmentID);
        }
    }

    public static boolean isLineContainsEnchantment(String line, Enchantment enchantment){
        if(enchantment == null) return false;
        return line.contains(I18n.translateToLocal(enchantment.getName()));
    }

    public static boolean isLineDesirable(ItemStack stack, String line, int lineNumber){
        if(KeyHandler.isKeyDown(KeyHandler.fullTooltip)) return true;

        if(ForgeConfigProvider.checkLineForLangKeys(line)) return true;
        if(ForgeConfigProvider.checkLineForPatterns(line)) return true;

        return false;
    }

    public static List<String> getDesirableTooltip(ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();
        List<String> originalLines = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        List<String> desirableLines = new ArrayList<>();

        NBTTagList enchantmentsNBT = ItemEnchantedBook.getEnchantments(stack);
        if(enchantmentsNBT.isEmpty() && stackHasBookmarkedEnchantments(stack)) enchantmentsNBT = stack.getEnchantmentTagList();

        Enchantment firstEnchantment = enchantmentsNBT.isEmpty() ? null : Enchantment.getEnchantmentByID(enchantmentsNBT.getCompoundTagAt(0).getShort("id"));
        int numEnchantments = 0;
        for (int i = 0; i < originalLines.size(); ++i) {
            String line = originalLines.get(i);
            boolean addLine = isLineDesirable(stack, line, i);

            if(!addLine){
                // Assume every enchant is next to each other
                if(isLineContainsEnchantment(line, firstEnchantment)) {
                    numEnchantments = stack.isItemEnchanted() && ForgeConfigHandler.enchantmentSearch.matchTopOnly ? 1 : enchantmentsNBT.tagCount();
                    firstEnchantment = null;
                }
                if(numEnchantments-- > 0) addLine = true;
            }

            if(addLine){
                if (i == 0) {
                    desirableLines.add(stack.getItem().getForgeRarity(stack).getColor() + line);
                }
                else {
                    desirableLines.add(TextFormatting.GRAY + line);
                }
            }
        }

        if(desirableLines.isEmpty()
                && !originalLines.isEmpty()
                && ForgeConfigHandler.client.emptyTooltipRender == ForgeConfigHandler.ClientConfig.EmptyTooltipRender.DISPLAY_NAME) {
            desirableLines.add(originalLines.get(0));
        }

        return desirableLines;
    }
}
