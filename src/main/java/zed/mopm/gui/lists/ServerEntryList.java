package zed.mopm.gui.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ServerSelectionList;
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

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private final ServerSaveLoadUtils entryListDetails;
    private final List<ServerEntry> relevantEntries;
    private final ModifiableMenu<MultiplayerMenu, ServerEntry, ServerEntryList> serverMenu;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public ServerEntryList(final ModifiableMenu<MultiplayerMenu, ServerEntry, ServerEntryList> serverSelection, final Minecraft clientIn, final int slotHeightIn) {
        super(serverSelection.getInvokeScreen(), clientIn, 0, 0, 0, 0, slotHeightIn);

        entryListDetails = new ServerSaveLoadUtils(clientIn);
        this.serverMenu = serverSelection;
        relevantEntries = new ArrayList<>();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: ServerSelectionList
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     * Gets the list entry from the displayed list of entries.
     *
     * @param index The index in the list of displayed list entries. This corresponds with the selected slot index.
     * @return returns the server entry at the displayed index.
     */
    @Override
    public ServerEntry getListEntry(final int index) {
        return this.relevantEntries.get(index);
    }

    /**
     * Gets the count of the currently displayed entries.
     *
     * @return returns the number of displayed entries.
     */
    @Override
    protected int getSize() {
        return this.relevantEntries.size();
    }

    //:: GuiListExtended
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public boolean mouseClicked(final int mouseX, final int mouseY, final int mouseEvent) {
        this.serverMenu.getInvokeScreen().selectRelevantServer(this.getSlotIndexFromScreenCoords(mouseX, mouseY));
        if (mouseEvent == 1 && this.getRelevantSelected() != -1) {
            this.mc.displayGuiScreen(new EditDirectory<>(this.serverMenu, mouseX, mouseY, false, this));
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, mouseEvent);
        }
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.getRelevantSelected();
    }

    //:: IModifiableList
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void rename(final int entryIndex, final String name) {
    	this.getListEntry(entryIndex).getServer().getServerData().serverName = name;
    	this.saveList();
    }

    @Override
    public void delete(final int entryIndex) {
        if (entryIndex != -1) {
            this.setRelevantServer(entryIndex);
            this.getListEntry(entryIndex).removeServer(this.serverMenu.getDirectoryList());
            this.deleteEntryAt(entryIndex);
            this.saveList();
            this.setRelevantServer(entryIndex - 1);
        }
        if (this.relevantEntries.isEmpty()) {
            this.serverMenu.getInvokeScreen().selectRelevantServer(-1);
        }
    }

    @Override
    public void changeDir(final int entryIndex) {
        this.setRelevantServer(entryIndex);
        final ServerEntry entry = this.getListEntry(entryIndex);
        entry.removeServer(this.serverMenu.getDirectoryList());
        this.mc.displayGuiScreen(new DirectorySelectionMenu(this.serverMenu, entry, new FolderList(this.serverMenu.getDirectoryList())));
    }

    //:: IListType
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void refresh() {
        this.serverMenu.getDirectoryList().populateDirectoryList(this.entryListDetails.getDetails(this.serverMenu.getInvokeScreen()));
    }

    @Override
    public void display(final List<ServerEntry> entries) {
        this.relevantEntries.clear();
        this.relevantEntries.addAll(entries);
        if (this.relevantEntries.isEmpty()) {
            this.serverMenu.getInvokeScreen().selectRelevantServer(-1);
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public void setRelevantServer(final int index) {
        this.selectedElement = index;
    }

    public int getRelevantSelected() {
        return this.selectedElement;
    }

    public ServerSaveData getSelectedServer() {
        return this.getListEntry(this.getRelevantSelected()).getServer();
    }

    public void editSelectedIndex(final ServerSaveData data) {
        final int index = this.getRelevantSelected();
        if (index != -1) {
            final ServerSaveData selected = this.getSelectedServer();
            final String oldPath = selected.getSavePath();
            if (!oldPath.equals(data.getSavePath())) {
                this.getListEntry(index).removeServer(this.serverMenu.getDirectoryList());
            }
            selected.copyFrom(data);
            selected.getServerData().pinged = false;
            this.entryListDetails.replace(this.getWholeIndex(index), selected);
        }
    }

    private void deleteEntryAt(final int entryIndex) {
        this.entryListDetails.removeSaveData(this.getWholeIndex(entryIndex));
        this.relevantEntries.remove(entryIndex);
        this.hardUpdate();
    }

    public void addServerSave(final ServerSaveData serverData) {
        this.entryListDetails.addSaveData(serverData);
    }

    public void loadList() {
        this.entryListDetails.loadServerList();
    }

    public void saveList() {
        this.entryListDetails.save();
    }

    public void hardUpdate() {
        this.serverMenu.refreshDirectoryEntryList();
    }

    private int getWholeIndex(final int partialIndex) {
        return this.relevantEntries.get(partialIndex).getListIndex();
    }
}
