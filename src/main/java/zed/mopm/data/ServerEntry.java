package zed.mopm.data;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import zed.mopm.api.data.IFolderPath;

import java.io.File;

public class ServerEntry extends ServerListEntryNormal implements GuiListExtended.IGuiListEntry, IFolderPath {
    protected ServerEntry(GuiMultiplayer ownerIn, ServerData serverIn) {
        super(ownerIn, serverIn);
    }

    @Override
    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {

    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {

    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

    }

    @Override
    public void setPath(String path) {

    }

    @Override
    public void setUniquePath(String path) {

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
