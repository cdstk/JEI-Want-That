package jeiwt.handlers;

import jeiwt.JEIWantThat;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ForgeConfigProvider {

    private static final Set<Pattern> tooltipPatterns = new HashSet<>();

    public static void init(){
        ForgeConfigProvider.initTooltipPatterns();
    }

    public static boolean checkLineForLangKeys(String line){
        for(String langKey : ForgeConfigHandler.tooltipLineSearch.langKeys){
            if(JEIWantThat.checkLangKey(langKey, line)) return true;
        }
        return false;
    }

    public static boolean checkLineForPatterns(String line){
        for (Pattern pattern : tooltipPatterns){
            if(pattern.matcher(line).matches()) return true;
        }
        return false;
    }

    public static void initTooltipPatterns(){
        ForgeConfigProvider.tooltipPatterns.clear();
        ForgeConfigProvider.tooltipPatterns.addAll(Arrays
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
}
