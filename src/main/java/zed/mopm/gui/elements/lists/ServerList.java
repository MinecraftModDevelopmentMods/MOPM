package zed.mopm.gui.elements.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.ServerSaveData;
import zed.mopm.gui.menus.base.SelectMenuBase;
import zed.mopm.gui.menus.base.ServerSelectMenu;
import zed.mopm.gui.menus.mutators.directory.SelectDirectoryMenu;
import zed.mopm.gui.menus.mutators.directory.EditDirectoryMenu;

import java.util.ArrayList;
import java.util.List;

public class ServerList
        extends GuiListExtended
        implements IModifiableList,
        IListType<ServerEntry> {

    //-----Fields:--------------------------------------//

    /**
     * The server list's entry details that help save and load entries.
     */
    private final ServerSaveLoadUtils entryListDetails;
    /**
     * The server entries that are currently being displayed.
     */
    private final List<ServerEntry> relevantEntries;
    /**
     * The containing selection menu of this list.
     */
    private final SelectMenuBase<
            ServerSelectMenu,
            ServerEntry,
            ServerList
            > serverMenu;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new server list.
     * @param serverSelection The menu the server list is contained in.
     * @param clientIn  The minecraft client in.
     * @param slotHeightIn The slot height of the server entries.
     */
    public ServerList(
            final SelectMenuBase<
                    ServerSelectMenu,
                    ServerEntry,
                    ServerList
                    > serverSelection,
            final Minecraft clientIn,
            final int slotHeightIn
    ) {
        super(clientIn, 0, 0, 0, 0, slotHeightIn);

        entryListDetails = new ServerSaveLoadUtils(clientIn);
        this.serverMenu = serverSelection;
        relevantEntries = new ArrayList<>();
    }

    //-----Overridden Methods:--------------------------//

    //:: ServerSelectionList
    //:::::::::::::::::::::::::::::://

    /**
     * Gets the list entry from the displayed list of entries.
     *
     * @param index The index in the list of displayed list entries.
     *              This corresponds with the selected slot index.
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
    //:::::::::::::::::::::::::::::://

    /**
     * Selects the server entry clicked on and connects
     * to a server entry if it was double clicked on.
     * @param slotIndex The slot index clicked.
     * @param isDoubleClick Determines if the click was a double click.
     * @param mouseX The x location of the mouse.
     * @param mouseY The y location of the mouse.
     */
    @Override
    protected final void elementClicked(
            final int slotIndex,
            final boolean isDoubleClick,
            final int mouseX,
            final int mouseY
    ) {
        this.setSelectedIndex(slotIndex);
        if (isDoubleClick) {
            this.serverMenu.getInvokeScreen().connectToSelected();
        }
    }

    /**
     * Selects the index of the server entry clicked on based on
     * where the mouse clicked. If the mouse click was a right click,
     * then the edit entry menu is opened.
     * @param mouseX The x location of the click
     * @param mouseY The y location of the click
     * @param mouseEvent The type of click.
     * @return Returns true dependant on where the mouse clicked.
     */
    @Override
    public final boolean mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseEvent
    ) {
        final int clickedIndex =
                this.getSlotIndexFromScreenCoords(
                        mouseX,
                        mouseY
                );

        if (mouseEvent == 0 && this.getSelectedIndex() == clickedIndex) {
            return super.mouseClicked(mouseX, mouseY, mouseEvent);
        }

        this.serverMenu.getInvokeScreen().selectRelevantServer(clickedIndex);
        if (mouseEvent == 1 && this.getSelectedIndex() != -1) {
            this.mc.displayGuiScreen(
                    new EditDirectoryMenu<>(
                            this.serverMenu,
                            mouseX,
                            mouseY,
                            false,
                            this
                    )
            );
            return true;
        }

        return false;
    }

    /**
     * Returns whether or not the server entry at slot index is selected.
     * @param slotIndex The index of a server entry.
     * @return Returns true if the server entry at the slot index is selected.
     * <br>
     * Returns false if the server entry at the slot index is not selected.
     */
    @Override
    protected final boolean isSelected(final int slotIndex) {
        return slotIndex == this.getSelectedIndex();
    }

    //:: IModifiableList
    //:::::::::::::::::::::::::::::://

    /**
     * Renames a server entry at the entry index.
     * @param entryIndex The index of the server entry to be renamed.
     * @param name The new name of the server entry.
     */
    @Override
    public final void rename(final int entryIndex, final String name) {
        this.getListEntry(entryIndex)
                .getServer()
                .getServerData()
                .serverName = name;
        this.entryListDetails.save();
    }

    /**
     * Deletes a server entry.
     * @param entryIndex The index of the server entry to delete.
     */
    @Override
    public final void delete(final int entryIndex) {
        if (entryIndex != -1) {
            this.setSelectedIndex(entryIndex);
            this.getListEntry(entryIndex)
                    .removeServer(this.serverMenu.getDirectoryList());
            this.deleteEntryAt(entryIndex);
            this.entryListDetails.save();
            this.setSelectedIndex(entryIndex - 1);
        }
        if (this.relevantEntries.isEmpty()) {
            this.serverMenu.getInvokeScreen().selectRelevantServer(-1);
        }
    }

    /**
     * Moves a server entry to a different directory.
     * @param entryIndex The index of the server entry to move.
     */
    @Override
    public final void changeDir(final int entryIndex) {
        this.setSelectedIndex(entryIndex);
        final ServerEntry entry = this.getListEntry(entryIndex);
        entry.removeServer(this.serverMenu.getDirectoryList());
        this.setSelectedIndex(entryIndex - 1);
        this.mc.displayGuiScreen(
                new SelectDirectoryMenu(
                        this.serverMenu,
                        entry,
                        new DirectoryList(this.serverMenu.getDirectoryList())
                )
        );
    }

    //:: IListType
    //:::::::::::::::::::::::::::::://

    /**
     * Refreshes the list of displayed entries.
     */
    @Override
    public final void refresh() {
        this.serverMenu.getDirectoryList()
                .populateDirectoryList(
                        this.entryListDetails.getDetails(
                                this.serverMenu.getInvokeScreen()
                        )
                );
    }

    /**
     * Sets the list of server entries to display on screen.
     * @param entries The server entries to display.
     */
    @Override
    public final void display(final List<ServerEntry> entries) {
        this.relevantEntries.clear();
        this.relevantEntries.addAll(entries);
        if (this.relevantEntries.isEmpty()) {
            this.serverMenu.getInvokeScreen().selectRelevantServer(-1);
        }
    }

    //-----This:----------------------------------------//

    /**
     * @return Returns the selected index within the server list.
     */
    public final int getSelectedIndex() {
        return this.selectedElement;
    }

    /**
     * Sets the selected index within the server list.
     * @param index The index of the server entry to select.
     */
    public final void setSelectedIndex(final int index) {
        this.selectedElement = index;
    }

    /**
     * @return Returns the server save data of the selected entry.
     */
    public final ServerSaveData getSelectedServer() {
        return this.getListEntry(this.getSelectedIndex()).getServer();
    }

    /**
     * Edits the details of the selected server entry.
     * @param data The new data to replace the selected server entry save data.
     */
    public final void editSelectedIndex(final ServerSaveData data) {
        final int index = this.getSelectedIndex();
        if (index != -1) {
            final ServerSaveData selected = this.getSelectedServer();
            final String oldPath = selected.getSavePath();
            if (!oldPath.equals(data.getSavePath())) {
                this.getListEntry(index)
                        .removeServer(
                                this.serverMenu.getDirectoryList()
                        );
            }
            selected.copyFrom(data);
            selected.getServerData().pinged = false;
            this.entryListDetails.replace(this.getWholeIndex(index), selected);
        }
    }

    /**
     * Deletes a server entry.
     * @param entryIndex The index of the server entry to be deleted.
     */
    private void deleteEntryAt(final int entryIndex) {
        this.entryListDetails.removeSaveData(this.getWholeIndex(entryIndex));
        this.relevantEntries.remove(entryIndex);
        this.updateServerList();
    }

    /**
     * Refreshes the directory list.
     */
    public final void updateServerList() {
        this.serverMenu.refreshDirectoryEntryList();
    }

    /**
     * @return Returns the server list entry details.
     */
    public final ServerSaveLoadUtils getListDetails() {
        return this.entryListDetails;
    }

    /**
     * @return Gets the size of the currently displayed list.
     */
    public final int getPartialSize() {
        return this.relevantEntries.size();
    }

    /**
     * @return Returns the index from a list of all of the created
     * server entries instead of the index within the currently displayed
     * server entry list.
     */
    public final int getWholeIndexFromSelected() {
        final int selected = this.getSelectedIndex();
        return selected != -1 ? this.getWholeIndex(selected) : -1;
    }

    /**
     * @param partialIndex The index within the relevant entries list.
     * @return Returns the index within the list of all the server entries.
     */
    public final int getWholeIndex(final int partialIndex) {
        return this.relevantEntries.get(partialIndex).getListIndex();
    }
}
