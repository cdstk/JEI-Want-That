package jeiwt.compat;

import jeiwt.mixin.hei.BookmarkItem_AccessorMixin;
import mezz.jei.bookmarks.BookmarkItem;

public class HadEnoughItemsUtil {

    public static boolean isBookmarkItem(Object ingredient) {
        return ingredient instanceof BookmarkItem;
    }

    public static Object getIngredientFromBookmark(Object ingredient) {
        if(ingredient instanceof BookmarkItem_AccessorMixin) {
            return ((BookmarkItem_AccessorMixin<?>) ingredient).jeiwt$getIngredient();
        }
        return null;
    }
}
