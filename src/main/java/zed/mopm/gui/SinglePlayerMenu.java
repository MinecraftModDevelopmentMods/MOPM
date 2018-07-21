package zed.mopm.gui;

import net.minecraft.client.gui.*;

import zed.mopm.api.gui.IMenuType;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.WorldList;
import zed.mopm.gui.mutators.CreateWorldEntryMenu;
import zed.mopm.util.References;

import java.io.IOException;
import java.util.List;

public class SinglePlayerMenu extends GuiWorldSelection implements IMenuType {

    private static final int EXIT_BTN_ID = 0;
    private static final int PLAY_BTN_ID = 1;
    private static final int DELET_BTN_ID = 2;
    private static final int EDIT_BTN_ID = 4;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public SinglePlayerMenu(GuiScreen screenIn) {
        super(screenIn);
    }

    @Override
    public void postInit() {
        this.buttonList.clear();
        super.postInit();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    protected void actionPerformed(GuiButton button, FolderList<WorldEntry> directories, WorldList selectionList) {
        try {
            WorldEntry selectedWorld = selectionList.getSelectedWorld();
            switch (button.id) {
                //:: Exit the world selection menu
                case 0:
                    this.mc.displayGuiScreen(this.prevScreen);
                    break;

                //:: Play selected world
                case 1:
                    if (selectedWorld != null) {
                        selectedWorld.joinWorld();
                    }
                    break;

                //:: Delete selected world
                case 2:
                    if (selectedWorld != null) {
                        selectedWorld.deleteWorld(directories);
                    }
                    break;

                //:: Edit selected world
                case 4:
                    if (selectedWorld != null) {
                        selectedWorld.editWorld();
                    }
                    break;

                //:: Recreate selected world
                case 5:
                    if (selectedWorld != null) {
                        selectedWorld.recreateWorld();
                    }
                    break;

                //:: This should not be reached
                default:
                    super.actionPerformed(button);
                    break;
            }
        }
        catch (IOException e) {
            References.LOG.error("", e);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    @Override
    public void invokeEntryCreation(ModifiableMenu menu) {
        this.mc.displayGuiScreen(new CreateWorldEntryMenu(this, new FolderList(menu.getDirectoryList())));
    }

    @Override
    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }

}
