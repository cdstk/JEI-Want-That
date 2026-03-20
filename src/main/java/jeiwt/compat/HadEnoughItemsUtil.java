package jeiwt.compat;

import mezz.jei.bookmarks.BookmarkItem;

public class HadEnoughItemsUtil {

    public static boolean isBookmarkItem(Object ingredient) {
        return ingredient instanceof BookmarkItem;
    }

    public static Object getIngredientFromBookmark(Object ingredient) {
        if(ingredient instanceof BookmarkItem) {
            return ((BookmarkItem<?>) ingredient).ingredient;
        }
        return null;
    }
}
