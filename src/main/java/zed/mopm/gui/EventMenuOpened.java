package zed.mopm.gui;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zed.mopm.util.References;

public class EventMenuOpened {
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        GuiScreen gui = event.getGui();

        References.LOG.info("GUI TYPE: " + gui);
        if (gui instanceof GuiWorldSelection) {
            References.LOG.info("Opening the SSP menu!");
        }
        else if (gui instanceof GuiMultiplayer) {
            References.LOG.info("Opening the SMP menu!");
        }
    }
}
