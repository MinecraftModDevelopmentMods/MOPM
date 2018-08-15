package zed.mopm.gui.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerList;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.ServerSaveData;
import zed.mopm.gui.ModifiableMenu;
import zed.mopm.gui.MultiplayerMenu;
import zed.mopm.gui.mutators.DirectorySelectionMenu;
import zed.mopm.gui.mutators.EditDirectory;

import java.util.ArrayList;
import java.util.List;

public class ServerEntryList extends ServerSelectionList implements IModifiableList, IListType<ServerEntry> {

    private final ServerSaveLoadUtils serverListDetails;
    private final List<ServerEntry> serverEntryList;
    private final List<ServerEntry> relevantServers;
    private final ModifiableMenu<MultiplayerMenu, ServerEntry, ServerEntryList> serverMenu;

    public ServerEntryList(final ModifiableMenu<MultiplayerMenu, ServerEntry, ServerEntryList> serverSelection, final Minecraft clientIn, final int slotHeightIn) {
        super(serverSelection.getInvokeScreen(), clientIn, 0, 0, 0, 0, slotHeightIn);

        serverListDetails = new ServerSaveLoadUtils(clientIn);
        this.serverMenu = serverSelection;
        relevantServers = new ArrayList<>();
        serverEntryList = new ArrayList<>();
    }

    @Override
    public ServerEntry getListEntry(final int index) {
        return this.relevantServers.get(index);
    }

    @Override
    protected int getSize() {
        return this.relevantServers.size();
    }

    @Override
    public boolean mouseClicked(final int mouseX, final int mouseY, final int mouseEvent) {
        if (mouseEvent == 1 && this.getSlotIndexFromScreenCoords(mouseX, mouseY) != -1) {
            this.mc.displayGuiScreen(new EditDirectory<>(this.serverMenu, mouseX, mouseY, false, this));
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, mouseEvent);
        }
    }

    @Override
    public void rename(final int entryIndex, final String name) {
    	relevantServers.get(entryIndex).getServer().getServerData().serverName = name;
    	this.saveList();
    }

    @Override
    public void delete(final int entryIndex) {
        if (entryIndex != -1) {
            this.setSelectedSlotIndex(entryIndex);
            this.getListEntry(entryIndex).removeServer(this.serverMenu.getDirectoryList());
            this.deleteEntryAt(entryIndex);
            this.updateServers();
            this.saveList();
        }
    }

    @Override
    public void changeDir(final int entryIndex) {
        this.setSelectedSlotIndex(entryIndex);
        final ServerEntry entry = this.getListEntry(entryIndex);
        entry.removeServer(this.serverMenu.getDirectoryList());
        this.mc.displayGuiScreen(new DirectorySelectionMenu(this.serverMenu, entry, new FolderList(this.serverMenu.getDirectoryList())));
    }

    @Override
    public void refresh() {
        this.updateServers();
        this.serverMenu.getDirectoryList().populateDirectoryList(this.serverEntryList);
    }

    @Override
    public void display(final List<ServerEntry> entries) {
        relevantServers.clear();
        relevantServers.addAll(entries);
    }

    public ServerSaveData getSelectedServer() {
        return this.getListEntry(this.getSelected()).getServer();
    }

    public ServerList getList() {
        final ServerList copy = new ServerList(this.mc);
        for (ServerEntry entry : this.serverEntryList) {
            copy.addServerData(entry.getServerData());
        }
        return copy;
    }

    public void editSelectedIndex(final ServerSaveData data) {
        if (this.selectedElement != -1) {

            final ServerSaveData selected = this.getSelectedServer();
            final String oldPath = selected.getSavePath();
            if (!oldPath.equals(data.getSavePath())) {
                this.getListEntry(this.getSelected()).removeServer(this.serverMenu.getDirectoryList());
            }
            selected.copyFrom(data);
        }
    }

    private void deleteEntryAt(final int entryIndex) {
        this.serverListDetails.removeSaveData(this.serverEntryList.indexOf(this.relevantServers.get(entryIndex)));
        this.relevantServers.remove(entryIndex);
    }

    public void addServerSave(final ServerSaveData serverData) {
        this.serverListDetails.addSaveData(serverData);
    }

    public void loadList() {
        this.serverListDetails.loadServerList();
    }

    public void saveList() {
        this.serverListDetails.save();
    }

    public void updateServers() {
        this.serverEntryList.clear();
        this.serverEntryList.addAll(this.serverListDetails.getDetails(this.serverMenu.getInvokeScreen()));
    }

    public void hardUpdate() {
        this.serverMenu.refreshDirectoryEntryList();
    }
}
