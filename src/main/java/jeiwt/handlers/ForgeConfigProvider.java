package jeiwt.handlers;

import jeiwt.JEIWantThat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ForgeConfigProvider {

    private static final Set<Pattern> TOOLTIP_PATTERNS = new HashSet<>();
    private static final Map<String, Collection<Pattern>> ITEM_TOOLTIP_PATTERNS = new HashMap<>();

    // TODO PR Ice and Fire to register Snow Villager on Forge
    private static final Set<ResourceLocation> VILLAGER_PROFESSIONS = new HashSet<>();
    private static ItemStack villagerTooltipItem = ItemStack.EMPTY;

    private static ItemStack playerTooltipItem = ItemStack.EMPTY;

    public static void init(){
        ForgeConfigProvider.initTooltipConfig();
        ForgeConfigProvider.initPlayerConfig();
        ForgeConfigProvider.initVillagerConfig();
    }

    public static int getSignedHexadecimal(String hex) {
        int value = Integer.MIN_VALUE;
        try {
            value = Integer.parseUnsignedInt(hex, 16);
        }
        catch (NumberFormatException ignored) {}
        return value;
    }

    public static boolean checkLineForLangKeys(String line){
        for(String langKey : ForgeConfigHandler.tooltipLineSearch.langKeys){
            if(JEIWantThat.checkLangKey(langKey, line)) return true;
        }
        return false;
    }

    public static boolean checkLineForPatterns(ItemStack stack, String line){
        // Exact ID Search
        ResourceLocation itemID = stack.getItem().getRegistryName();
        Collection<Pattern> itemPatterns = ITEM_TOOLTIP_PATTERNS.get(itemID.toString());
        if(itemPatterns != null) {
            for (Pattern pattern : itemPatterns){
                if(pattern.matcher(line).matches()) return true;
            }
        }

        // Mod ID Search
        itemPatterns = ITEM_TOOLTIP_PATTERNS.get(itemID.getNamespace());
        if(itemPatterns != null) {
            for (Pattern pattern : itemPatterns){
                if(pattern.matcher(line).matches()) return true;
            }
        }

        // Universal Search
        return checkLineForPatterns(line);
    }

    public static boolean checkLineForPatterns(String line){
        for (Pattern pattern : TOOLTIP_PATTERNS){
            if(pattern.matcher(line).matches()) return true;
        }
        return false;
    }

    public static boolean checkVillagerProfession(EntityVillager entityVillager){
        return VILLAGER_PROFESSIONS.contains(entityVillager.getProfessionForge().getRegistryName());
    }

    public static ItemStack getVillagerTooltipItem(){
        return villagerTooltipItem.copy();
    }

    public static ItemStack getPlayerTooltipItem() {
        return playerTooltipItem.copy();
    }

    public static void initTooltipConfig(){
        ForgeConfigProvider.TOOLTIP_PATTERNS.clear();
        ForgeConfigProvider.TOOLTIP_PATTERNS.addAll(Arrays
            .stream(ForgeConfigHandler.tooltipLineSearch.regexPatterns)
            .map(line -> {
                try{
                    return Pattern.compile(line);
                }
                catch (PatternSyntaxException e){
                    JEIWantThat.LOGGER.log(Level.WARN, "Regex syntax error: {}, ignoring", line);
                }
                return Pattern.compile("(?!)");
            }).collect(Collectors.toSet())
        );

        ForgeConfigProvider.ITEM_TOOLTIP_PATTERNS.clear();
        Arrays.stream(ForgeConfigHandler.tooltipLineSearch.perItemRegexPatterns).forEach(line -> {
            String[] split = line.split(",");
            if(split.length > 1) {
                String itemID = split[0];
                Pattern pattern = null;
                try {
                    String regex = line.replace(itemID + ",", "");
                    pattern = Pattern.compile(regex);
                }
                catch (PatternSyntaxException e){
                    JEIWantThat.LOGGER.log(Level.WARN, "Per Item Regex syntax error: {}, ignoring", line);
                }

                if(pattern != null) {
                    itemID = itemID.replace(":*", ""); // Wildcard is just modid
                    Collection<Pattern> itemPatterns = ForgeConfigProvider.ITEM_TOOLTIP_PATTERNS.computeIfAbsent(itemID, key -> new ArrayList<>());
                    itemPatterns.add(pattern);
                }
            }
        });
    }

    public static void initVillagerConfig(){
        String[] itemSplit = ForgeConfigHandler.villagerSearch.tooltipItemStyle.split(",");
        if(itemSplit.length > 0){
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemSplit[0].trim()));
            int meta = 0;
            if(itemSplit.length > 1){
                try {
                    meta = Integer.parseInt(itemSplit[1].trim());
                } catch (NumberFormatException e) {
                    JEIWantThat.LOGGER.log(Level.WARN, "Villager Tooltip Item invalid: {}, ignoring", (Object) itemSplit);
                }
            }
            if(item != null) villagerTooltipItem = new ItemStack(item, 1, meta);
        }

        ForgeConfigProvider.VILLAGER_PROFESSIONS.clear();
        Arrays.stream(ForgeConfigHandler.villagerSearch.villagerProfessions).forEach(line -> {
            if(line.equals("*")){
                VILLAGER_PROFESSIONS.addAll(ForgeRegistries.VILLAGER_PROFESSIONS.getKeys());
            }
            else {
                String[] split = line.split(":");
                boolean wildCard = split.length > 1 && split[1].equals("*");
                if(wildCard){
                    ForgeRegistries.VILLAGER_PROFESSIONS.getValuesCollection().forEach(profession -> {
                        if(profession.getRegistryName() != null && profession.getRegistryName().getNamespace().equals(split[0])){
                            VILLAGER_PROFESSIONS.add(profession.getRegistryName());
                        }
                    });
                }
                else {
                    // Work around for Ice and Fire
                    VILLAGER_PROFESSIONS.add(new ResourceLocation(line));
                }
            }
        });
    }

    public static void initPlayerConfig() {
        String[] itemSplit = ForgeConfigHandler.playerSearch.tooltipItemStyle.split(",");
        if (itemSplit.length > 0) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemSplit[0].trim()));
            int meta = 0;
            if (itemSplit.length > 1) {
                try {
                    meta = Integer.parseInt(itemSplit[1].trim());
                } catch (NumberFormatException e) {
                    JEIWantThat.LOGGER.log(Level.WARN, "Player Tooltip Item invalid: {}, ignoring", (Object) itemSplit);
                }
            }
            if (item != null) playerTooltipItem = new ItemStack(item, 1, meta);
        }
    }
}
