package zed.mopm.gui.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.ModifiableMenu;
import zed.mopm.gui.MultiplayerMenu;
import zed.mopm.gui.SinglePlayerMenu;
import zed.mopm.gui.lists.ServerEntryList;
import zed.mopm.gui.lists.WorldList;
import zed.mopm.util.References;


public class EventMenuOpened {
    private EventMenuOpened() { }

    private static ModifiableMenu<SinglePlayerMenu, WorldEntry, WorldList> worldSelection;
    private static ModifiableMenu<MultiplayerMenu, ServerEntry, ServerEntryList> serverSelection;

    private static SinglePlayerMenu sspMenu;
    private static MultiplayerMenu smpMenu;

    @SubscribeEvent
    public static void onGuiOpen(final GuiOpenEvent event) {
        GuiScreen gui = event.getGui();

        if (gui instanceof GuiWorldSelection) {
            References.LOG.info("GUI TYPE: " + gui + "\n" + worldSelection);
            References.LOG.info("Opening the SSP menu!");

            event.setGui(worldSelection);
        } else if (gui instanceof GuiMultiplayer) {
            References.LOG.info("GUI TYPE: " + gui + "/n" + serverSelection);
            References.LOG.info("Opening the SMP menu!");

            event.setGui(serverSelection);
        }
    }

    public static void loadMenus(final GuiScreen parentIn) {
        sspMenu = new SinglePlayerMenu(parentIn);
        smpMenu = new MultiplayerMenu(parentIn);

        worldSelection = new ModifiableMenu<>(sspMenu, Minecraft.getMinecraft());
        worldSelection.setContainingList(new WorldList(worldSelection, Minecraft.getMinecraft(), 36));

        serverSelection = new ModifiableMenu<>(smpMenu, Minecraft.getMinecraft());
        serverSelection.setContainingList(new ServerEntryList(serverSelection, Minecraft.getMinecraft(), 36));
    }
}
