package zed.mopm;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import zed.mopm.gui.events.EventMenuOpened;
import zed.mopm.util.References;

@Mod(
        modid = References.MODID,
        name = References.NAME,
        version = References.VERSION,
        certificateFingerprint = "@FINGERPRINT@",
        clientSideOnly = true
)
public class ModRegistry {

    //temp
    @Mod.EventHandler
    public void preModInit(final FMLPreInitializationEvent preInit) {
        // NOTHING TO SEE HERE
    }

    @Mod.EventHandler
    public void modInit(final FMLInitializationEvent init) {
        MinecraftForge.EVENT_BUS.register(EventMenuOpened.class);
    }

    @Mod.EventHandler
    public void modLoader(final FMLPostInitializationEvent postInit) {
        EventMenuOpened.loadMenus(Minecraft.getMinecraft().currentScreen);
    }

    @Mod.EventHandler
    public void onFingerprintViolation(final FMLFingerprintViolationEvent event) {
        References.LOG.warn(References.INVALID_FINGERPRINT);
    }
}
