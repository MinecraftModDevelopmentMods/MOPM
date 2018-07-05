package zed.mopm.data;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.api.data.IFolderPath;

public class WorldEntry extends GuiListWorldSelectionEntry implements GuiListExtended.IGuiListEntry, IFolderPath {
    private String pathToContainingDirectory;
    private String fileName;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public WorldEntry(GuiListWorldSelection listWorldSelIn, WorldSummary worldSummaryIn, ISaveFormat saveFormat) {
        super(listWorldSelIn, worldSummaryIn, saveFormat);
        fileName = worldSummaryIn.getFileName();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Interfaces:--------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiListExtended.IGuiListEntry
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected, partialTicks);
    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        return super.mousePressed(slotIndex, mouseX, mouseY, mouseEvent, relativeX, relativeY);
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        super.mouseReleased(slotIndex, x, y, mouseEvent, relativeX, relativeY);
    }

    @Override
    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
        super.updatePosition(slotIndex, x, y, partialTicks);
    }

    //:: IFolderPath
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void setPath(String path) {
        pathToContainingDirectory = path;
    }

    @Override
    public void setUniquePath(String path) {
        //Todo: to do
    }

    @Override
    public String getPathToDir() {
        return pathToContainingDirectory;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Interfaces:--------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public String getFileName() {
        return this.fileName;
    }
}
