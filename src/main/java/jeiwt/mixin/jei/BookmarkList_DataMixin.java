package jeiwt.mixin.jei;

import jeiwt.compat.HadEnoughItemsUtil;
import jeiwt.compat.ModLoadedUtil;
import jeiwt.handlers.ForgeConfigHandler;
import jeiwt.util.IBookmarkList_DataMixin;
import jeiwt.util.JEIUtil;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.ingredients.IIngredientListElement;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(BookmarkList.class)
public abstract class BookmarkList_DataMixin implements IBookmarkList_DataMixin {

    // TODO Fermium Booter Update for JEI vs HEI, currently implemented lazy way

    @Shadow (remap = false)
    public abstract List<IIngredientListElement<?>> getIngredientList();

    @Unique
    private static final Map<Item, Set<Integer>> ENCHANTMENTS_FROM_ITEMS = new HashMap<>();

    @Inject(
            method = "add*",
            at = @At(value = "RETURN"),
            remap = false
    )
    private <T> void jeiwt_jeiBookmarkList_addBookmarkData(T ingredient, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue()) {
            if (ingredient instanceof EnchantmentData) {
                jeiwt$addEnchantmentData((EnchantmentData) ingredient);
            } else if (ingredient instanceof ItemStack) {
                jeiwt$addItemStack((ItemStack) ingredient);
            }
        }
    }

    @Inject(
            method = "remove*",
            at = @At(value = "RETURN"),
            remap = false
    )
    private <T> void jeiwt_jeiBookmarkList_removeBookmarkData(T ingredient, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue()) {
            if (ingredient instanceof EnchantmentData) {
                jeiwt$removeEnchantmentData((EnchantmentData) ingredient);
            } else if (ingredient instanceof ItemStack) {
                jeiwt$removeItemStack((ItemStack) ingredient);
            }
        }
    }

    @Inject(
            method = "loadBookmarks",
            at = @At(value = "TAIL"),
            remap = false
    )
    private void jeiwt_jeiBookmarkList_loadBookmarksData(CallbackInfo ci){
        JEIUtil.BOOKMARK_LIST = (BookmarkList)(Object)this;
        jeiwt$initBookmarkedData();
    }

    @Inject(
            method = "notifyListenersOfChange",
            at = @At("HEAD"),
            remap = false
    )
    private void jeiwt_jeiBookmarkList_notifyListenersOfChangeForHEI(CallbackInfo ci){
        if(ModLoadedUtil.HAD_ENOUGH_ITEMS.isLoaded()) jeiwt$initBookmarkedData();
    }

    @Unique
    private void jeiwt$addEnchantmentData(EnchantmentData enchantmentData){
        JEIUtil.modifyBookmarkedEnchantments(Enchantment.getEnchantmentID(enchantmentData.enchantment));
    }

    @Unique
    private void jeiwt$removeEnchantmentData(EnchantmentData enchantmentData){
        JEIUtil.modifyBookmarkedEnchantments(Enchantment.getEnchantmentID(enchantmentData.enchantment), false);
    }

    @Unique
    private void jeiwt$addEnchantmentsFromItemStack(ItemStack stack) {
        if(Arrays.stream(ForgeConfigHandler.enchantmentSearch.bookmarkedItemIDs)
                .noneMatch(itemID -> ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemID)))){
            return;
        }

        Set<Integer> itemEnchantments = ENCHANTMENTS_FROM_ITEMS.computeIfAbsent(stack.getItem(), mapStack -> new HashSet<>());
        itemEnchantments.addAll(EnchantmentHelper.getEnchantments(stack).keySet().stream()
            .map(Enchantment::getEnchantmentID)
            .collect(Collectors.toSet())
        );
        itemEnchantments.forEach(JEIUtil::modifyEnchantmentsFromItems);
    }

    @Unique
    private void jeiwt$removeEnchantmentsFromItemStack(ItemStack stack) {
        if(!ENCHANTMENTS_FROM_ITEMS.containsKey(stack.getItem())) return;

        ENCHANTMENTS_FROM_ITEMS.remove(stack.getItem());
        Set<Integer> itemSet = EnchantmentHelper.getEnchantments(stack).keySet().stream()
            .map(Enchantment::getEnchantmentID)
            .collect(Collectors.toSet()
        );
        for(int id : itemSet){
            boolean shouldRemove = true;
            for(Set<Integer> savedSet : ENCHANTMENTS_FROM_ITEMS.values()){
                if (savedSet.contains(id)) {
                    shouldRemove = false;
                    break;
                }
            }
            if(shouldRemove) JEIUtil.modifyEnchantmentsFromItems(id, false);
        }
    }

    @Unique
    private void jeiwt$addItemStack(ItemStack stack){
        NBTTagList enchNBT = ItemEnchantedBook.getEnchantments(stack);
        if(!enchNBT.isEmpty()){
            for (int i = 0; i < enchNBT.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = enchNBT.getCompoundTagAt(i);
                int id = nbttagcompound.getShort("id");
                Enchantment enchantment = Enchantment.getEnchantmentByID(id);

                if (enchantment != null) {
                    JEIUtil.modifyBookmarkedEnchantments(id);
                }
            }
        }
        JEIUtil.modifyBookmarkedItems(stack, stack.getMetadata());
        jeiwt$addEnchantmentsFromItemStack(stack);
    }

    @Unique
    private void jeiwt$removeItemStack(ItemStack stack){
        NBTTagList enchNBT = ItemEnchantedBook.getEnchantments(stack);
        if(!enchNBT.isEmpty()){
            for (int i = 0; i < enchNBT.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = enchNBT.getCompoundTagAt(i);
                int id = nbttagcompound.getShort("id");
                JEIUtil.modifyBookmarkedEnchantments(id, false);
            }
        }
        JEIUtil.modifyBookmarkedItems(stack, stack.getMetadata(), false);
        jeiwt$removeEnchantmentsFromItemStack(stack);
    }

    @Unique
    @Override
    public void jeiwt$initBookmarkedData(){
        JEIUtil.initDesirables();
        this.getIngredientList().forEach(element -> {
            Object ingredient = element.getIngredient();
            if(ModLoadedUtil.HAD_ENOUGH_ITEMS.isLoaded() && HadEnoughItemsUtil.isBookmarkItem(ingredient)) {
                ingredient = HadEnoughItemsUtil.getIngredientFromBookmark(ingredient);
            }

            if(ingredient instanceof EnchantmentData){
                jeiwt$addEnchantmentData((EnchantmentData) ingredient);
            }
            else if(ingredient instanceof ItemStack){
                jeiwt$addItemStack((ItemStack) ingredient);
            }
        });
    }
}
