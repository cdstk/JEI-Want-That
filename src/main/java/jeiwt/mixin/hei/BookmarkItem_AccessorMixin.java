package jeiwt.mixin.hei;

import mezz.jei.bookmarks.BookmarkItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BookmarkItem.class)
public interface BookmarkItem_AccessorMixin<I> {

    @Accessor(value = "ingredient", remap = false)
    I jeiwt$getIngredient();
}
