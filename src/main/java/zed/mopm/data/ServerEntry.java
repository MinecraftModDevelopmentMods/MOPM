package zed.mopm.data;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import zed.mopm.api.data.IDrawableListEntry;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.gui.MultiplayerMenu;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.util.PathFormatter;

import java.io.File;

public class ServerEntry extends ServerListEntryNormal implements GuiListExtended.IGuiListEntry, IFolderPath, IDrawableListEntry {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private int x = 0;
    private int y = 0;

    private ServerSaveData server;
    private String lastIcon;

    private MultiplayerMenu owner;
    private String pathToContainingDirectory;
    private boolean lan;
    private int listIndex;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public ServerEntry(final GuiMultiplayer ownerIn, final ServerSaveData serverIn, final int listIndex) {
        super(ownerIn, serverIn.getServerData());
        this.owner = (MultiplayerMenu) ownerIn;
        this.server = serverIn;
        this.pathToContainingDirectory = PathFormatter.ensurePathFormat(serverIn.getSavePath());
        this.listIndex = listIndex;
        this.lan = false;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: IGuiListEntry/ServerListEntryNormal
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void updatePosition(final int slotIndex, final int x, final int y, final float partialTicks) {
    	//
    }

    @Override
    public void drawEntry(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected, final float partialTicks) {
    	super.drawEntry(this.listIndex, x, y, listWidth, slotHeight, mouseX, mouseY, this.owner.getSelectedIndex() == this.listIndex, partialTicks);
    	this.x = x;
    	this.y = y;
    	final String iconData = this.server.getServerData().getBase64EncodedIconData();
        if (iconData != null && !iconData.equals(this.lastIcon)) {
            this.lastIcon = iconData;
            this.owner.getServers().saveList();
        }
    }

    //:: IFolderPath
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void setPath(final String path) {
    	//
    }

    @Override
    public void setUniquePath(final String path) {
    	this.pathToContainingDirectory = PathFormatter.ensurePathFormat(path);
    	this.server.setSavePath(path);
    	this.owner.getServers().saveList();
    }

    @Override
    public String getPathToDir() {
        return pathToContainingDirectory;
    }

    @Override
    public File getMopmSaveFile() {
        return null;
    }

    //:: IDrawableListEntry
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public String drawableText() {
        return this.server.getServerData().serverName;
    }

    //:: Object
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public boolean equals(final Object o) {
        if (o instanceof ServerEntry) {
            final ServerEntry obj = (ServerEntry) o;
            return this.server == obj.server || this.listIndex == obj.listIndex && this.pathToContainingDirectory.equals(obj.pathToContainingDirectory);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.listIndex;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public void updateList() {
        this.owner.getServers().hardUpdate();
    }

    public ServerSaveData getServer() {
        return this.server;
    }

    public void removeServer(final FolderList removeFrom) {
        removeFrom.getBaseFolder().folderPath(this.pathToContainingDirectory).removeEntry(this.owner.getSelectedIndex());
    }

    public boolean isLan() {
        return this.lan;
    }

    public int getListIndex() {
        return this.listIndex;
    }
}
