package jeiwt.client.handlers;

import jeiwt.handlers.ForgeConfigHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyHandler {

    private static final IKeyConflictContext UNIVERSAL_NO_CONFLICTS = new IKeyConflictContext() {
        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }
    };

    private static boolean displayToggled = false;
    private static boolean modifiedToggled = false;
    private static boolean fullToggled = false;

    public static KeyBinding enableDisplay;
    public static KeyBinding modifiedTooltip;
    public static KeyBinding fullTooltip;

    public static void initKeybind() {
        enableDisplay = new KeyBinding(
                "key.jeiwt.enableDisplay",
                UNIVERSAL_NO_CONFLICTS,
                Keyboard.KEY_LCONTROL,
                "key.jeiwt.category"
        );
        modifiedTooltip = new KeyBinding(
                "key.jeiwt.modifiedTooltip",
                UNIVERSAL_NO_CONFLICTS,
                Keyboard.KEY_LSHIFT,
                "key.jeiwt.category"
        );
        fullTooltip = new KeyBinding(
                "key.jeiwt.fullTooltip",
                UNIVERSAL_NO_CONFLICTS,
                Keyboard.KEY_TAB,
                "key.jeiwt.category"
        );
        ClientRegistry.registerKeyBinding(enableDisplay);
        ClientRegistry.registerKeyBinding(modifiedTooltip);
        ClientRegistry.registerKeyBinding(fullTooltip);

        MinecraftForge.EVENT_BUS.register(KeyHandler.class);
    }

    public static boolean renderDisplay() {
        return ForgeConfigHandler.client.keybindsAsToggles ? displayToggled : isKeyDown(enableDisplay);
    }

    public static boolean renderModifiedTooltip() {
        return ForgeConfigHandler.client.keybindsAsToggles ? modifiedToggled : isKeyDown(modifiedTooltip);
    }

    public static boolean renderFullTooltip() {
        return ForgeConfigHandler.client.keybindsAsToggles ? fullToggled : isKeyDown(fullTooltip);
    }

    public static boolean isKeyDown(KeyBinding keyBinding){
        return isKeyboardKey(keyBinding.getKeyCode()) ? Keyboard.isKeyDown(keyBinding.getKeyCode()) : (keyBinding.isKeyDown() || keyBinding.isPressed());
    }

    public static boolean isKeyboardKeyDown(KeyBinding keyBinding){
        return isKeyboardKey(keyBinding.getKeyCode()) && isKeyDown(keyBinding);
    }

    public static boolean isMouseKeyDown(KeyBinding keyBinding){
        return !isKeyboardKey(keyBinding.getKeyCode()) && isKeyDown(keyBinding);
    }

    public static boolean isKeyboardKey(int keyCode) {
        return keyCode >= 0;
    }

    @SubscribeEvent
    public static void onGameplayKeyPress(InputEvent.KeyInputEvent event) {
        if(isKeyboardKeyDown(enableDisplay)) displayToggled = !displayToggled;
        if(isKeyboardKeyDown(modifiedTooltip)) modifiedToggled = !modifiedToggled;
        if(isKeyboardKeyDown(fullTooltip)) fullToggled = !fullToggled;
    }

    @SubscribeEvent
    public static void onGameplayMousePress(InputEvent.MouseInputEvent event) {
        if(isMouseKeyDown(enableDisplay)) displayToggled = !displayToggled;
        if(isMouseKeyDown(modifiedTooltip)) modifiedToggled = !modifiedToggled;
        if(isMouseKeyDown(fullTooltip)) fullToggled = !fullToggled;
    }

    @SubscribeEvent
    public static void onGuiKeyPressPre(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if(!event.getGui().isFocused()) {
            if(isKeyDown(enableDisplay)) displayToggled = !displayToggled;
            if(isKeyDown(modifiedTooltip)) modifiedToggled = !modifiedToggled;
            if(isKeyDown(fullTooltip)) fullToggled = !fullToggled;
        }
    }

    // Dedicated in Gui Mouse would go here
}