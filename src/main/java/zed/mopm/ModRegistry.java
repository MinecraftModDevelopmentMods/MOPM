package zed.mopm;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import zed.mopm.gui.events.EventMenuOpened;
import zed.mopm.util.References;

@Mod("MOPM")
public class ModRegistry {

	public ModRegistry() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::startClient);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void startClient(final FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(EventMenuOpened.class);
		EventMenuOpened.loadMenus(Minecraft.getInstance().currentScreen);
	}
}
