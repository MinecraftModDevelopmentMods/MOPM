package zed.mopm.data;

import java.io.*;

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
import zed.mopm.util.PathFormatter;
import zed.mopm.util.References;

public class WorldEntry extends GuiListWorldSelectionEntry implements GuiListExtended.IGuiListEntry, IFolderPath, IDrawableListEntry, Comparable<WorldEntry> {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

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

    public WorldEntry(final GuiListWorldSelection listWorldSelIn, final WorldSummary worldSummaryIn, final ISaveFormat saveFormat) {
        super(listWorldSelIn, worldSummaryIn, saveFormat);
        worldList = (WorldList) listWorldSelIn;
        summary = worldSummaryIn;
        fileName = worldSummaryIn.getFileName();
        mopmSaveData = saveFormat.getFile(fileName, MOPMLiterals.MOPM_SAVE_DAT);

        try (BufferedReader reader = new BufferedReader(new FileReader(mopmSaveData))) {
            pathToContainingDirectory = reader.readLine();
            pathToContainingDirectory = PathFormatter.ensurePathFormat(pathToContainingDirectory);
        } catch (IOException e) {
            References.LOG.error("", e);
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public String getFileName() {
        return this.fileName;
    }

    public void rename(final String name) {
        Minecraft.getMinecraft().getSaveLoader().renameWorld(this.fileName, name);
    }

    public void deleteWorld(final FolderList deleteFrom) {
        this.deleteWorld();
        removeWorld(deleteFrom);
    }

    public void removeWorld(final FolderList removeFrom) {
        removeFrom.getBaseFolder().folderPath(this.pathToContainingDirectory).removeEntry(this.worldList.getSelectedIndex());
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Interfaces/Overridden Methods:-------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiListExtended.IGuiListEntry
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    @Override
    public void drawEntry(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected, final float partialTicks) {
        super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected, partialTicks);
        this.x = x;
        this.y = y;
    }

    //:: IFolderPath
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void setPath(final String path) {
        this.pathToContainingDirectory = path;
    }

    @Override
    public void setUniquePath(final String path) {
        this.pathToContainingDirectory = path;
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(this.mopmSaveData))) {
            writer.write(path.getBytes());
        } catch (IOException e) {
            References.LOG.error("", e);
        }
        this.pathToContainingDirectory = PathFormatter.ensurePathFormat(pathToContainingDirectory);
    }

    @Override
    public String getPathToDir() {
        return pathToContainingDirectory;
    }

    @Override
    public File getMopmSaveFile() {
        return this.mopmSaveData;
    }

    //:: Object
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public boolean equals(final Object o) {
        return o instanceof WorldEntry &&  mopmSaveData.getAbsolutePath().equals(((WorldEntry) o).mopmSaveData.getAbsolutePath());
    }

    @Override
    public int hashCode() {
        return this.summary.hashCode();
    }

    //:: Comparable
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public int compareTo(final WorldEntry o) {
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
