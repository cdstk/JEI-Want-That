package jeiwt.client.handlers;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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

    public static boolean isKeyDown(KeyBinding keyBinding){
        return Keyboard.isKeyDown(keyBinding.getKeyCode());
    }
}