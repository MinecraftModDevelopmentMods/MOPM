package zed.mopm.gui;

import net.minecraft.client.gui.*;

import zed.mopm.api.gui.IMenuType;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.WorldList;
import zed.mopm.gui.mutators.CreateEntryMenu;
import zed.mopm.gui.mutators.CreateWorldEntryMenu;
import zed.mopm.util.References;

import java.io.IOException;
import java.util.List;

public class SinglePlayerMenu extends GuiWorldSelection implements IMenuType {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private static final int EXIT_BTN_ID = 0;
    private static final int PLAY_BTN_ID = 1;
    private static final int DELETE_BTN_ID = 2;
    private static final int EDIT_BTN_ID = 4;
    private static final int RECREATE_BTN_ID = 5;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public SinglePlayerMenu(final GuiScreen screenIn) {
        super(screenIn);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiWorldSelection
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void postInit() {
        this.buttonList.clear();
        super.postInit();
    }

    protected void actionPerformed(final GuiButton button, final FolderList<WorldEntry> directories, final WorldList selectionList) {
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

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int mouseButton) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    //:: IMenuType
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void listInit(IListType list) {
        //:: do nothing
        // - There is no extra list initialization that needs to be done.
    }

    @Override
    public void invokeEntryCreation(final ModifiableMenu menu) {
        this.mc.displayGuiScreen(new CreateEntryMenu<CreateWorldEntryMenu, WorldEntry>(new CreateWorldEntryMenu(menu), new FolderList<>(menu.getDirectoryList())));
    }

    @Override
    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }

}
