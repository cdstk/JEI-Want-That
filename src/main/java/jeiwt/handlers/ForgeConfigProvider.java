package jeiwt.handlers;

import jeiwt.JEIWantThat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ForgeConfigProvider {

    private static final Set<Pattern> TOOLTIP_PATTERNS = new HashSet<>();
    // TODO PR Ice and Fire to register Snow Villager on Forge
    private static final Set<ResourceLocation> VILLAGER_PROFESSIONS = new HashSet<>();
    private static ItemStack villagerTooltipItem = ItemStack.EMPTY;

    public static void init(){
        ForgeConfigProvider.initTooltipConfig();
        ForgeConfigProvider.initVillagerConfig();
    }

    public static boolean checkLineForLangKeys(String line){
        for(String langKey : ForgeConfigHandler.tooltipLineSearch.langKeys){
            if(JEIWantThat.checkLangKey(langKey, line)) return true;
        }
        return false;
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

    public static void initTooltipConfig(){
        ForgeConfigProvider.TOOLTIP_PATTERNS.clear();
        ForgeConfigProvider.TOOLTIP_PATTERNS.addAll(Arrays
            .stream(ForgeConfigHandler.tooltipLineSearch.regexPatterns)
            .map(line -> {
                try{
                    return Pattern.compile(line);
                }
                catch (PatternSyntaxException e){
                    JEIWantThat.LOGGER.log(Level.WARN, "Regex syntax error: {}, ignoring", (Object) line);
                }
                return Pattern.compile("(?!)");
            }).collect(Collectors.toSet())
        );
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
}
