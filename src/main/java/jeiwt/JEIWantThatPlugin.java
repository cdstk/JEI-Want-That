package jeiwt;

import fermiumbooter.FermiumRegistryAPI;
import jeiwt.compat.ModLoadedUtil;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class JEIWantThatPlugin implements IFMLLoadingPlugin {

	public JEIWantThatPlugin() {
		MixinBootstrap.init();

		FermiumRegistryAPI.enqueueMixin(false, "mixins.jeiwt.vanilla.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.jeiwt.jei.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.jeiwt.srp.json", FermiumRegistryAPI.isModPresent(ModLoadedUtil.SRP_MODID));
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) { }
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}