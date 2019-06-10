package zed.mopm.data;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.api.data.IDrawableListEntry;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.gui.elements.lists.DirectoryList;
import zed.mopm.gui.elements.lists.WorldList;
import zed.mopm.gui.menus.base.SelectMenuBase;
import zed.mopm.gui.menus.mutators.entries.CreateEntryMenu;
import zed.mopm.gui.menus.mutators.entries.CreateWorldMenu;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.PathFormatter;
import zed.mopm.util.References;

public class WorldEntry
        extends GuiListWorldSelectionEntry
        implements GuiListExtended.IGuiListEntry,
        IFolderPath,
        IDrawableListEntry,
        Comparable<WorldEntry> {

    //-----Fields:--------------------------------------//

    /**
     * The vanilla world summery data for this world entry.
     */
    private WorldSummary summary;
    /**
     * The containing world list for this world entry.
     */
    private WorldList worldList;
    /**
     * The path to the directory where this world entry is contained.
     */
    private String pathToContainingDirectory;
    /**
     * The mopm world save file name.
     */
    private String worldFileName;
    /**
     * A reference to the world save file to write the containing path to.
     */
    private File mopmSaveData;

    /**
     * The world entry's x location in the containing list.
     */
    private int xLoc;
    /**
     * The wold entry's y location in the containing list.
     */
    private int yLoc;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new world entry.
     * @param listWorldSelIn The list the entry will be stored in.
     * @param worldSummaryIn The vanilla world details.
     * @param saveFormat The vanilla save format.
     */
    public WorldEntry(
            final GuiListWorldSelection listWorldSelIn,
            final WorldSummary worldSummaryIn,
            final ISaveFormat saveFormat
    ) {
        super(listWorldSelIn, worldSummaryIn, saveFormat);
        worldList = (WorldList) listWorldSelIn;
        summary = worldSummaryIn;
        worldFileName = worldSummaryIn.getFileName();
        mopmSaveData = saveFormat.getFile(
                worldFileName,
                MOPMLiterals.MOPM_SAVE_DAT
        );

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(mopmSaveData)
                        )
        ) {
            pathToContainingDirectory = reader.readLine();
            pathToContainingDirectory =
                    PathFormatter.ensurePathFormat(pathToContainingDirectory);
        } catch (IOException e) {
            References.LOG.error("", e);
        }
    }

    //-----This:----------------------------------------//

    /**
     * @return Returns the mopm save file name. The name is the
     * same as the world.
     */
    public final String getWorldFileName() {
        return this.worldFileName;
    }

    /**
     * Renames the world file.
     * @param name The new name of the world file.
     */
    public final void rename(final String name) {
        Minecraft.getMinecraft().getSaveLoader()
                .renameWorld(this.worldFileName, name);
    }

    /**
     * Deletes this world entry from the directory list and on disk.
     * @param deleteFrom The directory list to delete the world from.
     */
    public final void deleteWorld(final DirectoryList deleteFrom) {
        this.deleteWorld();
        removeWorld(deleteFrom);
    }

    /**
     * Removes the world entry from the directory list only.
     * @param removeFrom The list to remove the world entry from.
     */
    public final void removeWorld(final DirectoryList removeFrom) {
        removeFrom.getBaseFolder()
                .folderPath(this.pathToContainingDirectory)
                .removeEntry(this.worldList.getSelectedIndex());
    }

    //-----Interfaces/Overridden Methods:---------------//

    //:: GuiListWorldSelectionEntry
    //:::::::::::::::::::::::::::::://

    /**
     * Run when a world entry is being recreated. This method recreates
     * the world and brings up the world creation menu.
     */
    @Override
    public final void recreateWorld() {
        final Minecraft client = Minecraft.getMinecraft();
        final SelectMenuBase selectMenu = this.worldList.getWorldMenu();
        final CreateWorldMenu recreateWorld = new CreateWorldMenu(selectMenu);
        final ISaveHandler handler = client.getSaveLoader()
                .getSaveLoader(this.summary.getFileName(), false);
        final WorldInfo worldInfo = handler.loadWorldInfo();

        handler.flush();
        if (worldInfo != null) {
            recreateWorld.recreateFromExistingWorld(worldInfo);
            client.displayGuiScreen(
                    new CreateEntryMenu<>(
                            recreateWorld,
                            selectMenu.getDirectoryList()
                    )
            );
        }
    }

    //:: GuiListExtended.IGuiListEntry
    //:::::::::::::::::::::::::::::://

    /**
     * Draws the world entry.
     * @param slotIndex The slot index of the entry.
     * @param x The x location of the world entry.
     * @param y The y location of the world entry.
     * @param listWidth The width of the containing list.
     * @param slotHeight The slot height of the world entry.
     * @param mouseX The x location of the mouse.
     * @param mouseY The y location of the mouse.
     * @param isSelected Determines if the world entry is selected.
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
        super.drawEntry(slotIndex,
                x,
                y,
                listWidth,
                slotHeight,
                mouseX,
                mouseY,
                isSelected,
                partialTicks
        );
        this.xLoc = x;
        this.yLoc = y;
    }

    //:: IFolderPath
    //:::::::::::::::::::::::::::::://

    /**
     * Sets the containing directory path location.
     * @param path The new path.
     */
    @Override
    public final void setPath(final String path) {
        this.pathToContainingDirectory = path;
    }

    /**
     * Sets the unique containing path to the world entry.
     * @param path The unique path to the world entry.
     */
    @Override
    public final void setUniquePath(final String path) {
        this.pathToContainingDirectory = path;
        try (
                DataOutputStream writer =
                        new DataOutputStream(
                                new FileOutputStream(this.mopmSaveData)
                        )
        ) {
            writer.write(path.getBytes());
        } catch (IOException e) {
            References.LOG.error("", e);
        }
        this.pathToContainingDirectory =
                PathFormatter.ensurePathFormat(pathToContainingDirectory);
    }

    /**
     * @return Returns the path to the containing directory of the world entry.
     */
    @Override
    public final String getPathToDir() {
        return pathToContainingDirectory;
    }

    /**
     * @return Returns the save file where the path to the containing directory
     * is saves.
     */
    @Override
    public final File getMopmSaveFile() {
        return this.mopmSaveData;
    }

    //:: Object
    //:::::::::::::::::::::::::::::://

    /**
     * Determines if an object is equivalent to this world entry.
     * @param o The object to compare to.
     * @return True if the object is equal to this world entry.
     */
    @Override
    public final boolean equals(final Object o) {
        return o instanceof WorldEntry
                &&  mopmSaveData.getAbsolutePath()
                .equals(
                        ((WorldEntry) o).mopmSaveData.getAbsolutePath()
                );
    }

    /**
     * @return Returns the world summary hash code.
     */
    @Override
    public final int hashCode() {
        return this.summary.hashCode();
    }

    //:: Comparable
    //:::::::::::::::::::::::::::::://

    /**
     * Compares another world entry with this world entry.
     * @param o The world entry to compare to this world entry.
     * @return Returns the summary's compareTo().
     */
    @Override
    public final int compareTo(final WorldEntry o) {
        return this.summary.compareTo(o.summary);
    }

    //:: IDrawableListEntry
    //:::::::::::::::::::::::::::::://

    /**
     * @return Returns the x location of this world entry.
     */
    @Override
    public final int getX() {
        return this.xLoc;
    }

    /**
     * @return Returns the y location of this world entry.
     */
    @Override
    public final int getY() {
        return this.yLoc;
    }

    /**
     * @return Returns the world file name.
     */
    @Override
    public final String getEntryText() {
        return this.worldFileName;
    }
}
