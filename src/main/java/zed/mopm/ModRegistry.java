package zed.mopm;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import zed.mopm.systems.disk.SaveLoadManager;

import java.io.IOException;

@Mod("mopm")
public class ModRegistry {

	public ModRegistry() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::startClient);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void startClient(final FMLClientSetupEvent event) {
		try {
			SaveLoadManager.get().loadMOPM();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
