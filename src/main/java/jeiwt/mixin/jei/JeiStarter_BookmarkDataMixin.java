package jeiwt.mixin.jei;

import com.llamalad7.mixinextras.sugar.Local;
import jeiwt.util.JEIUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.textures.Textures;
import mezz.jei.startup.JeiStarter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(JeiStarter.class)
public abstract class JeiStarter_BookmarkDataMixin {

    @Inject(
            method = "start",
            at = @At("TAIL"),
            remap = false
    )
    private void jeiwt_jeiJeiStarter_startGetBookmarkList(List<IModPlugin> plugins, Textures textures, CallbackInfo ci, @Local BookmarkList bookmarkList){
        JEIUtil.BOOKMARK_LIST = bookmarkList;
    }
}
