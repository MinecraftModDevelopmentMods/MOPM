package zed.mopm.data;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WorldEntry extends GuiListWorldSelectionEntry implements GuiListExtended.IGuiListEntry, IFolderPath, Comparable<WorldEntry> {
    private WorldSummary summary;
    private String pathToContainingDirectory;
    private String fileName;
    private File mopmSaveData;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public WorldEntry(GuiListWorldSelection listWorldSelIn, WorldSummary worldSummaryIn, ISaveFormat saveFormat) {
        super(listWorldSelIn, worldSummaryIn, saveFormat);
        summary = worldSummaryIn;
        fileName = worldSummaryIn.getFileName();
        mopmSaveData = saveFormat.getFile(fileName, MOPMLiterals.MOPM_SAVE);

        try (BufferedReader reader = new BufferedReader(new FileReader(mopmSaveData))) {
            pathToContainingDirectory = reader.readLine();
        }
        catch (IOException e) {
            References.LOG.error("", e);
        }
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
        // Not in use
    }

    @Override
    public void setUniquePath(String path) {
        // Not in use
    }

    @Override
    public String getPathToDir() {
        return pathToContainingDirectory;
    }

    @Override
    public File getMopmSaveData() {
        return this.mopmSaveData;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Interfaces:--------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public String getFileName() {
        return this.fileName;
    }

    @Override
    public boolean equals(Object o) {
        return !(o instanceof WorldEntry) ? false : mopmSaveData.getAbsolutePath().equals(((WorldEntry) o).mopmSaveData.getAbsolutePath());
    }

    @Override
    public int hashCode() {
        return this.summary.hashCode();
    }

    @Override
    public int compareTo(WorldEntry o) {
        return this.summary.compareTo(o.summary);
    }
}
