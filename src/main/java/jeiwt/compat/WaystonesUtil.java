package jeiwt.compat;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

public class WaystonesUtil {

    public static boolean isBlockWaystone(Block block) {
        return block instanceof BlockWaystone;
    }

    public static boolean isWaystoneBase(IBlockState blockState){
        return blockState.getValue(BlockWaystone.BASE);
    }

    public static boolean isTileWaystone(TileEntity tileEntity) {
        return tileEntity instanceof TileWaystone;
    }

    public static boolean isWaystoneKnown(TileEntity tileEntity) {
        return ClientWaystones.getKnownWaystone(((TileWaystone)tileEntity).getWaystoneName()) != null;
    }

    public static boolean isWaystoneNatural(TileEntity tileEntity) {
        return ((TileWaystone)tileEntity).wasGenerated();
    }
}
