package jeiwt.compat;

import jeiwt.util.JEIUtil;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import svenhjol.charm.crafting.block.BlockCrate;
import svenhjol.charm.crafting.tile.TileCrate;
import svenhjol.meson.MesonItemBlock;
import svenhjol.meson.helper.ItemNBTHelper;

public class CharmUtil {

    private static boolean doCharmRender = true;

    public static boolean isTileCrate(TileEntity tileEntity) {
        return tileEntity instanceof TileCrate;
    }

    public static boolean checkNestedCrate(ItemStack stack){
        if(stack.getItem() instanceof MesonItemBlock
                && ((MesonItemBlock) stack.getItem()).getBlock() instanceof BlockCrate
                && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = ItemNBTHelper.getCompound(stack, "BlockEntityTag");
            if (!tagCompound.isEmpty()
                    && tagCompound.hasKey("inventory")
                    && !tagCompound.getCompoundTag("inventory").getTagList("Items", 10).isEmpty()) {
                NonNullList<ItemStack> itemList = NonNullList.withSize(9, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(tagCompound.getCompoundTag("inventory"), itemList);
                for(ItemStack innerStack : itemList){
                    if(JEIUtil.isItemStackDesirable(innerStack)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doCharmRender(){
        return doCharmRender;
    }

    public static void setSkipCharmRender(){
        doCharmRender = false;
    }

    public static void resetSkipCharmRender(){
        doCharmRender = true;
    }
}
