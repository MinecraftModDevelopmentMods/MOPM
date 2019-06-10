package zed.mopm.data;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import zed.mopm.api.data.IDrawableListEntry;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.gui.menus.base.ServerSelectMenu;
import zed.mopm.gui.elements.lists.DirectoryList;
import zed.mopm.util.PathFormatter;

import java.io.File;

public class ServerEntry
        extends ServerListEntryNormal
        implements GuiListExtended.IGuiListEntry,
        IFolderPath,
        IDrawableListEntry {

    //-----Fields:--------------------------------------//

    /**
     * The relative x location of the server entry.
     */
    private int xLoc = 0;
    /**
     * The relative y location of the server entry.
     */
    private int yLoc = 0;

    /**
     * The server save data the defines the server entry.
     */
    private ServerSaveData server;
    /**
     * The last icon displayed for the server entry.
     */
    private String lastIcon;

    /**
     * The containing selection menu.
     */
    private ServerSelectMenu owner;
    /**
     * The path the contains this entry.
     */
    private String pathToContainingDirectory;
    /**
     * True if the server is lan. <br>
     * False if the server is online.
     */
    private boolean lan;
    /**
     * The index where the server entry is located within the
     * selection list.
     */
    private int listIndex;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new server entry.
     * @param ownerIn The containing selection menu.
     * @param serverIn The server data.
     * @param index The list index of the new entry.
     */
    public ServerEntry(
            final GuiMultiplayer ownerIn,
            final ServerSaveData serverIn,
            final int index
    ) {
        super(ownerIn, serverIn.getServerData());
        this.owner = (ServerSelectMenu) ownerIn;
        this.server = serverIn;
        this.pathToContainingDirectory =
                PathFormatter.ensurePathFormat(serverIn.getSavePath());
        this.listIndex = index;
        this.lan = false;
    }

    //-----Overridden Methods:--------------------------//

    //:: IGuiListEntry/ServerListEntryNormal
    //:::::::::::::::::::::::::::::://

    /**
     * Draws the server entry within the selection list.
     * @param slotIndex The slot index of the server entry.
     * @param x The relative x location of the server entry.
     * @param y The relative y location of the server entry.
     * @param listWidth The width of the containing list.
     * @param slotHeight The height of the containing list slot.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param isSelected Determines if the slot index has focus.
     * @param partialTicks The partial game ticks.
     */
    @Override
    public final void drawEntry(
            final int slotIndex,
            final int x,
            final int y,
            final int listWidth,
            final int slotHeight,
            final int mouseX,
            final int mouseY,
            final boolean isSelected,
            final float partialTicks
    ) {
        boolean isFocused =
                this.owner.getServers()
                        .getWholeIndexFromSelected()
                        == this.listIndex;
        super.drawEntry(
                slotIndex,
                x,
                y,
                listWidth,
                slotHeight,
                mouseX,
                mouseY,
                isFocused,
                partialTicks
        );

        this.xLoc = x;
        this.yLoc = y;
        final String iconData =
                this.server.getServerData()
                        .getBase64EncodedIconData();
        if (iconData != null && !iconData.equals(this.lastIcon)) {
            this.lastIcon = iconData;
            this.owner.getServers().getListDetails().save();
        }
    }

    //:: IFolderPath
    //:::::::::::::::::::::::::::::://

    /**
     * This is not in use because ServerSaveDate deals with setting
     * the server entry directory path.
     * @param path The directory path where this entry is located.
     */
    @Override
    public final void setPath(final String path) {
        // Not in use since ServerSaveData deals with setting the path.
    }

    /**
     * Sets the uniquely named path of where this server entry is located.
     * @param path The uniquely named path.
     */
    @Override
    public final void setUniquePath(final String path) {
        this.pathToContainingDirectory = PathFormatter.ensurePathFormat(path);
        this.server.setSavePath(path);
        this.owner.getServers().getListDetails().save();
    }

    /**
     * @return Returns the location in which this server entry is located.
     */
    @Override
    public final String getPathToDir() {
        return pathToContainingDirectory;
    }

    /**
     * This is not in use so it returns null. <br>
     * There is no mopm save file for server entries because server
     * data is stored within the vanilla servers.dat file.
     * @return Returns null.
     */
    @Override
    public final File getMopmSaveFile() {
        return null;
    }

    //:: IDrawableListEntry
    //:::::::::::::::::::::::::::::://

    /**
     * @return Returns the x position of this server entry.
     */
    @Override
    public final int getX() {
        return this.xLoc;
    }

    /**
     * @return Returns the y position of this server entry.
     */
    @Override
    public final int getY() {
        return this.yLoc;
    }

    /**
     * @return Returns the server name.
     */
    @Override
    public final String getEntryText() {
        return this.server.getServerData().serverName;
    }

    //:: Object
    //:::::::::::::::::::::::::::::://

    /**
     * Determines if an object is equivalent to this server entry.
     * @param o The object to compare to.
     * @return Returns true if the object is equivalent.
     */
    @Override
    public final boolean equals(final Object o) {
        if (o instanceof ServerEntry) {
            final ServerEntry obj = (ServerEntry) o;
            return this.server == obj.server
                    || this.listIndex == obj.listIndex
                    && this.pathToContainingDirectory.equals(
                            obj.pathToContainingDirectory
            );
        }
        return false;
    }

    /**
     * @return Returns the list index.
     */
    @Override
    public final int hashCode() {
        return this.listIndex;
    }

    //-----This:----------------------------------------//

    /**
     * Updates the server list of the containing selection menu.
     */
    public final void updateList() {
        this.owner.getServers().updateServerList();
    }

    /**
     * @return Returns the server entry details.
     */
    public final ServerSaveData getServer() {
        return this.server;
    }

    /**
     * Removes this server entry from the directory list.
     * @param removeFrom The directory list to remove this server entry from.
     */
    public final void removeServer(final DirectoryList removeFrom) {
        removeFrom.getBaseFolder()
                .folderPath(this.pathToContainingDirectory)
                .removeEntry(this.owner.getServers().getSelectedIndex());
    }

    /**
     * @return Returns true if this entry is a lan server.
     * Returns false if this entry is an online server.
     */
    public final boolean isLan() {
        return this.lan;
    }

    /**
     * @return Returns the list index of this server entry.
     */
    public final int getListIndex() {
        return this.listIndex;
    }
}
