package zed.mopm.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.api.data.IDrawableListEntry;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.WorldList;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.*;

public class WorldEntry extends GuiListWorldSelectionEntry implements GuiListExtended.IGuiListEntry, IFolderPath, IDrawableListEntry, Comparable<WorldEntry> {
    private WorldSummary summary;
    private WorldList worldList;
    private String pathToContainingDirectory;
    private String fileName;
    private File mopmSaveData;

    private int x;
    private int y;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public WorldEntry(GuiListWorldSelection listWorldSelIn, WorldSummary worldSummaryIn, ISaveFormat saveFormat) {
        super(listWorldSelIn, worldSummaryIn, saveFormat);
        worldList = (WorldList) listWorldSelIn;
        summary = worldSummaryIn;
        fileName = worldSummaryIn.getFileName();
        mopmSaveData = saveFormat.getFile(fileName, MOPMLiterals.MOPM_SAVE);

        try (BufferedReader reader = new BufferedReader(new FileReader(mopmSaveData))) {
            pathToContainingDirectory = reader.readLine();
            this.ensurePathFormat();
        }
        catch (IOException e) {
            References.LOG.error("", e);
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private void ensurePathFormat() {
        if (pathToContainingDirectory.startsWith(MOPMLiterals.BASE_DIR + "/")) {
            this.pathToContainingDirectory = this.pathToContainingDirectory.substring((MOPMLiterals.BASE_DIR + "/").length());
        }
        else {
            this.pathToContainingDirectory = "";
        }
    }

    public String getFileName() {
        return this.fileName;
    }

    public void rename(String name) {
        Minecraft.getMinecraft().getSaveLoader().renameWorld(this.fileName, name);
    }

    public void deleteWorld(FolderList deleteFrom) {
        this.deleteWorld();
        removeWorld(deleteFrom);
    }

    public void removeWorld(FolderList removeFrom) {
        removeFrom.getBaseFolder().folderPath(this.pathToContainingDirectory).removeEntry(this.worldList.getSelectedIndex());
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Interfaces/Overridden Methods:-------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiListExtended.IGuiListEntry
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected, partialTicks);
        this.x = x;
        this.y = y;
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
        this.pathToContainingDirectory = path;
    }

    @Override
    public void setUniquePath(String path) {
        this.pathToContainingDirectory = path;
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(this.mopmSaveData))) {
            writer.write(path.getBytes());
        }
        catch (IOException e) {
            References.LOG.error("", e);
        }
        this.ensurePathFormat();
    }

    @Override
    public String getPathToDir() {
        return pathToContainingDirectory;
    }

    @Override
    public File getMopmSaveData() {
        return this.mopmSaveData;
    }

    //:: Object
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public boolean equals(Object o) {
        return !(o instanceof WorldEntry) ? false : mopmSaveData.getAbsolutePath().equals(((WorldEntry) o).mopmSaveData.getAbsolutePath());
    }

    @Override
    public int hashCode() {
        return this.summary.hashCode();
    }

    //:: Comparable
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public int compareTo(WorldEntry o) {
        return this.summary.compareTo(o.summary);
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
        return this.fileName;
    }
}
