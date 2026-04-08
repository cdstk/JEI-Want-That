package jeiwt.compat;

public class QuarkUtil {

    private static boolean doQuarkRender = true;

    public static boolean doQuarkRender(){
        return doQuarkRender;
    }

    public static void setSkipQuarkRender(){
        doQuarkRender = false;
    }

    public static void resetSkipQuarkRender(){
        doQuarkRender = true;
    }
}
