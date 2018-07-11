package zed.mopm;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import zed.mopm.gui.events.EventMenuOpened;
import zed.mopm.util.References;

@Mod(modid = References.MODID, name = References.NAME, version = References.VERSION, certificateFingerprint = "@FINGERPRINT@")
public class ModRegistry {

    @SidedProxy(clientSide = References.CLIENTSIDE)
    public static ClientProxy proxy;

    @Mod.EventHandler
    public void preModInit(FMLPreInitializationEvent preInit) {

    }

    @Mod.EventHandler
    public void modInit(FMLInitializationEvent init) {
        MinecraftForge.EVENT_BUS.register(EventMenuOpened.class);
    }

    @Mod.EventHandler
    public void modLoader(FMLPostInitializationEvent postInit) {
        EventMenuOpened.loadMenus(Minecraft.getMinecraft().currentScreen);
    }

    @Mod.EventHandler
    public void onFingerprintViolation(final FMLFingerprintViolationEvent event) {
        References.LOG.warn(References.INVALID_FINGERPRINT);
    }

}
