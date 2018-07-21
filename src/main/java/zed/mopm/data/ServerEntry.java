package zed.mopm.data;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import zed.mopm.api.data.IFolderPath;

import java.io.File;

public class ServerEntry extends ServerListEntryNormal implements GuiListExtended.IGuiListEntry, IFolderPath {
    protected ServerEntry(final GuiMultiplayer ownerIn, final ServerData serverIn) {
        super(ownerIn, serverIn);
    }

    @Override
    public void updatePosition(final int slotIndex, final int x, final int y, final float partialTicks) {
    	//
    }

    @Override
    public void drawEntry(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected, final float partialTicks) {
    	//
    }

    @Override
    public boolean mousePressed(final int slotIndex, final int mouseX, final int mouseY, final int mouseEvent, final int relativeX, final int relativeY) {
        return false;
    }

    @Override
    public void mouseReleased(final int slotIndex, final int x, final int y, final int mouseEvent, final int relativeX, final int relativeY) {
    	//
    }

    @Override
    public void setPath(final String path) {
    	//
    }

    @Override
    public void setUniquePath(final String path) {
    	//
    }

    @Override
    public String getPathToDir() {
        return null;
    }

    @Override
    public File getMopmSaveData() {
        return null;
    }

}
