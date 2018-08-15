package zed.mopm.gui;

import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import zed.mopm.api.data.ServerDataStatus;
import zed.mopm.api.gui.IMenuType;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.ServerSaveData;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.ServerEntryList;
import zed.mopm.gui.mutators.CreateServerEntryMenu;

import java.util.List;

public class MultiplayerMenu extends GuiMultiplayer implements IMenuType {

    private static final int EDITING_ID = 7;
    private static final int JOIN_ID = 1;
    private static final int CANCEL_ID = 0;
    private static final int DELETE_ID = 2;
    private static final int REFRESH_ID = 8;
    private static final int DIRECT_CONN_ID = 4;

    private ServerEntryList serverList;
    private ServerSaveData saveData;

    boolean listInitialized = false;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public MultiplayerMenu(final GuiScreen parentScreen) {
        super(parentScreen);
        this.saveData = new ServerSaveData();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        super.initGui();
        this.selectServer(-1);
    }

    protected void actionPerformed(final GuiButton button, final ModifiableMenu menu) {
        switch (button.id) {
            case EDITING_ID:
                this.saveData.changeStatus(ServerDataStatus.EDITING);
                this.saveData.copyFrom(this.serverList.getSelectedServer());
                this.mc.displayGuiScreen(new CreateServerEntryMenu(menu, this.saveData, new FolderList(menu.getDirectoryList())));
                break;

            case JOIN_ID:
                this.connectToSelected();
                break;

            case CANCEL_ID:
                this.mc.displayGuiScreen(null);
                break;

            case DELETE_ID:
                this.saveData.changeStatus(ServerDataStatus.REMOVING);
                this.confirmClicked(true, 0);
                break;

            case REFRESH_ID:
                this.mc.displayGuiScreen(menu);
                break;

            case DIRECT_CONN_ID:
                this.saveData.changeStatus(ServerDataStatus.DIRECT_CONNECTING);
                this.saveData.copyFrom(new ServerSaveData());
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.saveData.getServerData()));
                break;

            default:
                break;
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

    @Override
    public void connectToSelected() {
        final int selected = this.serverList.getSelected();
        final ServerEntry entry = selected < 0 ? null : this.serverList.getListEntry(selected);

        if (entry == null) {
            return;
        }
        else if (entry.isLan()) {
            //:: TODO: Implement lan worlds
        }
        else {
            this.connectToServer(entry.getServerData());
        }
    }

    private void connectToServer(final ServerData server) {
        net.minecraftforge.fml.client.FMLClientHandler.instance().connectToServer(this, server);
    }

    @Override
    public void selectServer(final int index) {
        if (listInitialized) {
            this.serverList.setSelectedSlotIndex(index);
            super.selectServer(index);
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {

        if (result) {
            switch (saveData.getStatus()) {
                case ADDING:
                    this.serverList.addServerSave(this.saveData);
                    this.serverList.saveList();
                    this.serverList.setSelectedSlotIndex(-1);
                    break;

                case EDITING:
                    this.serverList.editSelectedIndex(this.saveData);
                    this.serverList.saveList();
                    this.serverList.refresh();
                    break;

                case REMOVING:
                    this.serverList.delete(this.serverList.getSelected());
                    break;

                case DIRECT_CONNECTING:
                    this.connectToServer(this.saveData.getServerData());
                    break;

                case NONE:
                    //:: Do nothing
                    break;
            }

            saveData.changeStatus(ServerDataStatus.NONE);
        }
        else {
            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    public void invokeEntryCreation(final ModifiableMenu menu) {
        this.saveData.changeStatus(ServerDataStatus.ADDING);
        this.saveData.copyFrom(new ServerSaveData());
        this.mc.displayGuiScreen(new CreateServerEntryMenu(menu, this.saveData, new FolderList(menu.getDirectoryList())));
    }

    @Override
    public void listInit(final IListType list) {
        if (!this.listInitialized) {
            this.listInitialized = true;
            this.serverList = (ServerEntryList) list;
            this.serverList.loadList();
            this.selectServer(-1);
        }
    }

    @Override
    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }

    public ServerEntryList getServers() {
        return serverList;
    }

    public int getSelectedIndex() {
        return this.serverList.getSelected();
    }
}
