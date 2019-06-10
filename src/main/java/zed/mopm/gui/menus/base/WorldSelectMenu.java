package zed.mopm.gui.menus.base;

import net.minecraft.client.gui.*;

import zed.mopm.api.gui.IMenuType;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.elements.lists.DirectoryList;
import zed.mopm.gui.elements.lists.WorldList;
import zed.mopm.gui.menus.mutators.entries.CreateEntryMenu;
import zed.mopm.gui.menus.mutators.entries.CreateWorldMenu;
import zed.mopm.util.References;

import java.io.IOException;
import java.util.List;

public class WorldSelectMenu
        extends GuiWorldSelection
        implements IMenuType {

    //-----Constants:-----------------------------------//

    /**
     * The id of the exit button.
     */
    private static final int EXIT_BTN_ID = 0;
    /**
     * The id of the play world button.
     */
    private static final int PLAY_BTN_ID = 1;
    /**
     * The id of the delete world button.
     */
    private static final int DELETE_BTN_ID = 2;
    /**
     * The id of the edit world button.
     */
    private static final int EDIT_BTN_ID = 4;
    /**
     * The id of the recreate world button.
     */
    private static final int RECREATE_BTN_ID = 5;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new world selection menu.
     * @param screenIn The parent main menu.
     */
    public WorldSelectMenu(final GuiScreen screenIn) {
        super(screenIn);
    }

    //-----Overridden Methods:--------------------------//

    //:: GuiWorldSelection
    //:::::::::::::::::::::::::::::://

    /**
     * Initializes the super world selection menu.
     */
    @Override
    public final void postInit() {
        this.buttonList.clear();
        super.postInit();
    }

    /**
     * Handles the action of a clicked button.
     * @param button The button that was clicked.
     * @param directories The list of directories.
     * @param selectionList The list of world entries.
     */
    protected final void actionPerformed(
            final GuiButton button,
            final DirectoryList<WorldEntry> directories,
            final WorldList selectionList
    ) {
        try {
            WorldEntry selectedWorld = selectionList.getSelectedWorld();
            switch (button.id) {
                //:: Exit the world selection menu
                case EXIT_BTN_ID:
                    this.mc.displayGuiScreen(this.prevScreen);
                    break;

                //:: Play selected world
                case PLAY_BTN_ID:
                    if (selectedWorld != null) {
                        selectedWorld.joinWorld();
                    }
                    break;

                //:: Delete selected world
                case DELETE_BTN_ID:
                    if (selectedWorld != null) {
                        selectedWorld.deleteWorld(directories);
                    }
                    break;

                //:: Edit selected world
                case EDIT_BTN_ID:
                    if (selectedWorld != null) {
                        selectedWorld.editWorld();
                    }
                    break;

                //:: Recreate selected world
                case RECREATE_BTN_ID:
                    if (selectedWorld != null) {
                        selectedWorld.recreateWorld();
                    }
                    break;

                //:: This should not be reached
                default:
                    super.actionPerformed(button);
                    break;
            }
        } catch (IOException e) {
            References.LOG.error("", e);
        }
    }

    /**
     * Do nothing here because this is handled by
     * the selection menu.
     * @param mouseX N/A
     * @param mouseY N/A
     * @param mouseButton N/A
     */
    @Override
    public void mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    /**
     * Do nothing here because this is handled by
     * the selection menu.
     * @param mouseX N/A
     * @param mouseY N/A
     * @param mouseButton N/A
     */
    @Override
    public void mouseReleased(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    //:: IMenuType
    //:::::::::::::::::::::::::::::://

    /**
     * Do nothing here because this is handled by
     * the selection menu.
     * @param list N/A
     */
    @Override
    public void listInit(final IListType list) {
        //:: do nothing
        // - There is no extra list initialization that needs to be done.
    }

    /**
     * Invokes the creation of a world entry.
     * @param menu The selection menu.
     */
    @Override
    public final void invokeEntryCreation(final SelectMenuBase menu) {
        this.mc.displayGuiScreen(
                new CreateEntryMenu<CreateWorldMenu, WorldEntry>(
                        new CreateWorldMenu(menu),
                        new DirectoryList<>(menu.getDirectoryList())
                )
        );
    }

    /***
     * @return Returns the list of world selection menu buttons.
     */
    @Override
    public final List<GuiButton> getButtonList() {
        return this.buttonList;
    }

}
