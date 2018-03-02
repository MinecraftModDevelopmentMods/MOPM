package zed.mopm;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import zed.mopm.gui.EventMenuOpened;
import zed.mopm.util.References;

@Mod(modid = References.MODID, name = References.NAME, version = References.VERSION)
public class ModRegistry {

    @SidedProxy(clientSide = References.CLIENTSIDE)
    public static ClientProxy proxy;

    @Mod.EventHandler
    public void ModPreInitilization(FMLPreInitializationEvent preInit) {

    }

    @Mod.EventHandler
    public void ModInitilization(FMLInitializationEvent init) {
        FMLCommonHandler.instance().bus().register(new EventMenuOpened());
    }

    @Mod.EventHandler
    public void ModLoader(FMLPostInitializationEvent postInit) {

    }
}
