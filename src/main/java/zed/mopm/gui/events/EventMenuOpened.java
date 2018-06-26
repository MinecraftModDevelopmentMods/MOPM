package zed.mopm.gui.events;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zed.mopm.gui.MultiplayerMenu;
import zed.mopm.gui.SinglePlayerMenu;
import zed.mopm.util.References;

public class EventMenuOpened {
    private static SinglePlayerMenu sspMenu;
    private static MultiplayerMenu smpMenu;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        GuiScreen gui = event.getGui();

        References.LOG.info("GUI TYPE: " + gui);
        if (gui instanceof GuiWorldSelection) {
            References.LOG.info("Opening the SSP menu!");

            event.setGui(sspMenu);
        }
        else if (gui instanceof GuiMultiplayer) {
            References.LOG.info("Opening the SMP menu!");

            event.setGui(smpMenu);
        }
    }

    public static void loadMenus(GuiScreen parentIn) {
        sspMenu = new SinglePlayerMenu(parentIn);
        smpMenu = new MultiplayerMenu(parentIn);
    }
}
