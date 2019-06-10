package zed.mopm.gui.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.menus.base.SelectMenuBase;
import zed.mopm.gui.menus.base.ServerSelectMenu;
import zed.mopm.gui.menus.base.WorldSelectMenu;
import zed.mopm.gui.elements.lists.ServerList;
import zed.mopm.gui.elements.lists.WorldList;
import zed.mopm.util.References;


public final class EventMenuOpened {

    /**
     * The message logged to the console when opening the world
     * selection menu.
     */
    private static final String SSP_OPEN = "Opening the SSP menu!";
    /**
     * The message logged to the console when opening the server
     * selection menu.
     */
    private static final String SMP_OPEN = "Opening the SMP menu!";
    /**
     * The slot height of the selection lists.
     */
    private static final int SLOT_HEIGHT = 36;

    /**
     * The world selection menu that is opened when this event is fired
     * on the vanilla world selection menu.
     */
    private static SelectMenuBase<
            WorldSelectMenu,
            WorldEntry,
            WorldList
            > worldSelection;
    /**
     * The server selection menu that is opened when this event is fired
     * on the vanilla server selection menu.
     */
    private static SelectMenuBase<
            ServerSelectMenu,
            ServerEntry,
            ServerList
            > serverSelection;

    /**
     * This should not be accessed.
     */
    private EventMenuOpened() { }

    /**
     * Opens the modded menus.
     * @param event The modded menu to open.
     */
    @SubscribeEvent
    public static void onGuiOpen(final GuiOpenEvent event) {
        GuiScreen gui = event.getGui();

        if (gui instanceof GuiWorldSelection) {
            References.LOG.info(SSP_OPEN);
            event.setGui(worldSelection);
        } else if (gui instanceof GuiMultiplayer) {
            References.LOG.info(SMP_OPEN);
            event.setGui(serverSelection);
        }
    }

    /**
     * Instantiates the modded selection menus.
     * @param parentIn The main menu.
     */
    public static void loadMenus(final GuiScreen parentIn) {
        worldSelection = new SelectMenuBase<>(
                new WorldSelectMenu(parentIn),
                Minecraft.getMinecraft()
        );
        worldSelection.setContainingList(
                new WorldList(
                        worldSelection,
                        Minecraft.getMinecraft(),
                        SLOT_HEIGHT
                )
        );

        serverSelection = new SelectMenuBase<>(
                new ServerSelectMenu(parentIn),
                Minecraft.getMinecraft()
        );
        serverSelection.setContainingList(
                new ServerList(
                        serverSelection,
                        Minecraft.getMinecraft(),
                        SLOT_HEIGHT
                )
        );
    }
}
