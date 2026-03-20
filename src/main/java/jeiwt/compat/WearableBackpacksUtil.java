package jeiwt.compat;

import net.mcft.copy.backpacks.block.entity.TileEntityBackpack;
import net.minecraft.tileentity.TileEntity;

public class WearableBackpacksUtil {

    public static boolean isTileBackpack(TileEntity tileEntity) {
        return tileEntity instanceof TileEntityBackpack;
    }
}
