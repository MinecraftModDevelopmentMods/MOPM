package zed.mopm.data;

import com.google.common.collect.Lists;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.gui.elements.lists.ServerSaveLoadUtils;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.List;
import java.util.Deque;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.ArrayDeque;

/**
 * K can be ServerListEntryNormal.
 * K can be GuiListWorldSelectionEntry.
 *
 * @param <K> The type that the directory tree is able to store.
 */
public class DirectoryTree<K> {

    //-----Fields:--------------------------------------//

    /**
     * Represents the layer of depth this directory is located
     * in.
     */
    private int depth;
    /**
     * This is the index of this directory within the list of leaves.
     */
    private int index;
    /**
     * The name of this directory.
     */
    private String name;
    /**
     * The unique identifying name of this directory.
     */
    private String uniqueName;

    /**
     * The list of server or world entries this directory contains.
     */
    private List<K> entries;
    /**
     * The list of directories this directory contains.
     */
    private List<Directory> directoryLeaves;
    /**
     * The navigational structure in relation to this directory.
     */
    private Map<String, DirectoryTree<K>> navTree;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new directory tree with a containing directory nameIn.
     * @param nameIn The name of the containing directory.
     */
    public DirectoryTree(final String nameIn) {
        navTree = new HashMap<>();
        directoryLeaves = new ArrayList<>();
        entries = new ArrayList<>();

        this.name = nameIn;
        this.uniqueName = nameIn + "#0";
        this.depth = 0;
        this.index = 0;
    }

    /**
     * Creates a new directory that will be contained within another directory.
     * @param nameIn The name of the new directory.
     * @param depthIn The depth of the new directory.
     * @param indexIn The list index of the new directory.
     */
    private DirectoryTree(
            final String nameIn,
            final int depthIn,
            final int indexIn
    ) {
        this(nameIn);
        this.index = indexIn;
        this.uniqueName = nameIn + "#" + index;
        this.depth = depthIn;
    }

    /**
     * Creates a deep copy of a DirectoryTree passed to it.
     * @param copyFrom The DirectoryTree to copy from.
     */
    public DirectoryTree(final DirectoryTree<K> copyFrom) {
        this(copyFrom.name, copyFrom.depth, copyFrom.index);

        this.entries = Lists.newArrayList(copyFrom.entries);
        this.directoryLeaves = Lists.newArrayList(copyFrom.directoryLeaves);

        for (String key : copyFrom.navTree.keySet()) {
            this.navTree.put(
                    key,
                    new DirectoryTree(copyFrom.navTree.get(key)
                    )
            );
        }
    }

    //-----Builders:------------------------------------//

    /**
     * Creates a new directory in the called upon directory.
     *
     * @param nameIn The name of the new directory.
     * @return Returns the newly created directory.
     */
    public DirectoryTree<K> newFolder(final String nameIn) {
        DirectoryTree<K> newFolder = new DirectoryTree(
                nameIn,
                depth + 1,
                this.navTree.size()
        );
        navTree.put(newFolder.uniqueName, newFolder);
        directoryLeaves.add(new Directory(nameIn, newFolder.uniqueName));
        return newFolder;
    }

    /**
     * Inserts an entry into into the called upon directory.
     *
     * @param entry The entry to be added to the directory.
     * @return Returns the directory that was called upon.
     */
    public DirectoryTree<K> newEntry(final K entry) {
        this.entries.remove(entry);
        entries.add(entry);
        return this;
    }

    //-----Navigation:----------------------------------//

    /**
     * @param indexIn List location index
     * @return Returns the subdirectory located at index within
     * the called upon directory.
     */
    public DirectoryTree<K> stepDown(final int indexIn) {
        return navTree.get(this.directoryLeaves.get(indexIn).dirUUID());
    }

    /**
     * @param nameIn The directory that is being searched for.
     * @return Returns the subdirectory with the name 'name' within
     * the called upon directory.
     */
    public DirectoryTree<K> stepDown(final String nameIn) {
        return navTree.get(nameIn);
    }

    /**
     * @param pathIn The location in the directory tree to be returned.
     * @return Returns the subdirectory defined by the end of the path.
     * @throws NoSuchElementException Thrown if the path does not exist.
     */
    public DirectoryTree<K> folderPath(final String pathIn) {
        DirectoryTree<K> current = this;
        if (!pathIn.isEmpty()) {
            for (String part : pathIn.split("/")) {
                if (current.navTree.containsKey(part)) {
                    current = current.navTree.get(part);
                } else {
                    throw new NoSuchElementException();
                }
            }
        }

        return current;
    }

    /**
     * Gets The directory at index i.
     * @param i index
     * @return returns the directory at i
     */
    public Directory getDirectory(final int i) {
        return directoryLeaves.get(i);
    }

    /**
     * Returns the entry of the current directory located in i.
     * @param i index
     * @return returns the entry at i
     */
    public K getEntry(final int i) {
        return this.entries.get(i);
    }

    /**
     * @return Returns the list of directory entries in this directory.
     */
    public List<K> getEntries() {
        return this.entries;
    }

    //-----Utilities:-----------------------------------//

    //:: Getters
    //:::::::::::::::::::::::::::::://

    /**
     * Returns the name of the called upon directory.
     * Do not use this method to get the name of a directory if the name
     * is being used to traverse a directory path.
     *
     * Refer to getUniqueName();
     *
     * @return Returns the vanity name of the directory.
     */
    public String displayName() {
        return this.name;
    }

    /**
     * Every directory has a display name and a navigational name.
     * The navigational name is what is referred to in order to
     * distinguish between two directoryLeaves within the same
     * directory that have the same name.<br>
     * <br>
     * <b>For instance:</b> <br>
     * The base directory may contain directoryLeaves with the names
     * of "Directory 1" and "Directory 1". In order to distinguish
     * between the two, the unique name always has a '#' with a
     * number append to the end of it.<br><br>
     * <b>Therefore:</b> <br>
     * - base#0 <br><br>
     * <b>contains:</b> <br>
     * <ul>
     *  <li>Directory 1#0</li>
     *  <li>Directory 2#1</li>
     * </ul>
     *
     * The corresponding numbers are the index in which they appear in
     * the list of directoryLeaves.
     *
     * @return Returns the unique name of the directory.
     */
    public String getUniqueName() {
        return this.uniqueName;
    }

    /**
     * The base directory has a depth of 0. <br>
     * Every subdirectory within one another have an incremented depth. <br>
     * Therefore directoryLeaves within the base directory will have a
     * depth of 1. <br><br>
     *
     * <b>Visually represented:</b><br>
     * <b>path:</b><br>
     * base/directory1/directory2/directory3 <br>
     * - base<b>[depth 0]</b><br>
     * - directory1<b>[depth 1]</b><br>
     * - directory2<b>[depth 2]</b><br>
     * - directory3<b>[depth 3]</b><br><br>
     *
     * This method will return the placement in the path of the called
     * upon directory.
     *
     * @return Returns the subdirectory count.
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     * @return Returns the number of directoryLeaves contained in the called
     * upon directory.
     */
    public int folders() {
        return navTree.size();
    }

    /**
     * @return Returns the number of entries contained within the called
     * upon directory.
     */
    public int entries() {
        return entries.size();
    }

    /**
     * @return Returns the total amount of directoryLeaves and entries contained
     * within the called upon directory.
     */
    public int size() {
        return navTree.size() + entries.size();
    }

    /**
     * Converts the called upon directory object to a String.
     * The String will represent the branching directory structure of the object
     * starting at the called upon directory as the root.
     *
     * @param showEntries True: append directoryLeaves's entries to the String
     *                    False: do not append entries to the String
     * @return Returns the String representation of the branching directory
     * structure.
     */
    private String listDirectories(final boolean showEntries) {
        StringBuilder str = new StringBuilder();
        this.listDirectories(showEntries, "", str);
        return str.toString();
    }

    /**
     * @see #listDirectories(boolean)
     * @param showEntries True: append directoryLeaves's entries to the String
     *                    False: do not append entries to the String
     * @param depthIn Current folder depth.
     * @param str The String to append to.
     */
    private void listDirectories(
            final boolean showEntries,
            final String depthIn,
            final StringBuilder str
    ) {
        str.append(depthIn)
                .append(this.uniqueName)
                .append(':')
                .append('\n');
        for (Directory dir : this.directoryLeaves) {
            this.navTree.get(dir.dirUUID())
                    .listDirectories(showEntries, depthIn + "\t", str);
        }
        if (showEntries) {
            for (K entry : this.entries) {
                str.append(depthIn)
                        .append("- ")
                        .append(entry.toString())
                        .append('\n');
            }
        }
    }

    //--------------------------------------------------//

    //:: Section - Actions
    //:::::::::::::::::::::::::::::://

    //:: Modifying the directory
    //:::::::::::::::::::::::::::::://

    /**
     * Renames a directory in the called upon directory.
     *
     * @param indexIn The directory index that will be renamed.
     * @param nameIn The new name for the directory.
     */
    public void renameDir(final int indexIn, final String nameIn) {
        DirectoryTree<K> temp = this.stepDown(indexIn);
        String oldName = temp.uniqueName;

        this.navTree.remove(oldName);

        temp.name = nameIn;
        temp.uniqueName = nameIn
                + this.uniqueName.substring(
                        this.uniqueName.lastIndexOf('#')
        );

        this.navTree.put(temp.uniqueName, temp);

        Directory tempDir = this.directoryLeaves.get(indexIn);
        tempDir.setDirName(temp.name);
        tempDir.setDirUUID(temp.uniqueName);

    }

    /**
     * @see #removeDir(String)
     * @param indexIn The index used to reference what subdirectory should
     *                be removed from the called upon directory.
     * @return returns true if the directory at i was successfully removed.
     * false otherwise.
     */
    public boolean removeDir(final int indexIn) {
        return removeDir(this.directoryLeaves.get(indexIn).dirUUID());
    }

    /**
     * Attempts to remove a subdirectory from the called upon containing
     * directory. <br><br>
     * A check for worlds/servers within the subdirectory or any subdirectories
     * of the subdirectory (and onwards to the end of every branch of the
     * directory tree)is made, and will move any containing worlds/servers
     * within any of the containing subdirectories to the immutable base
     * directory. This is to prevent the loss of world visually not showing
     * within any of the compatible menus.
     *
     * @see #safeRemoveDir(String) This method is what does the actual remove
     *                             of the subdirectory. removeDir() does all
     *                             the necessairy checks before a subdirectory
     *                             can be safely removed.
     *
     * @param dirName The subdirectory's name that will be removed from the
     *                containing directory. The containing directory is the
     *                directory that is being called upon.
     * @return Returns true if the directory was successful deleted.<br>
     *         Returns false if there was an issue when attempting to remove
     *         a subdirectory.
     */
    public boolean removeDir(final String dirName) {
        if (!this.navTree.containsKey(dirName)) {
            return false;
        }

        //:: Looks into the directory to see if
        // - worlds or servers need to be moved to
        // - the base directory.
        final DirectoryTree<K> temp = this.navTree.get(dirName);
        boolean hasEntry = temp.entries.isEmpty();

        if (!hasEntry) {
            if (temp.entries.get(0) instanceof WorldEntry) {
                hasEntry = writeWorldsToBase(
                        (DirectoryTree<WorldEntry>) temp
                );
            }
            if (temp.entries.get(0) instanceof ServerEntry) {
                hasEntry = writeServersToBase(
                        (DirectoryTree<ServerEntry>) temp
                );
            }
        }

        Map<String, DirectoryTree<K>> rmvFromTree;
        rmvFromTree = new HashMap<>(temp.navTree);
        for (final String key : rmvFromTree.keySet()) {
            hasEntry = temp.removeDir(key);
            temp.navTree = rmvFromTree;
            rmvFromTree = new HashMap<>(rmvFromTree);
        }

        safeRemoveDir(dirName);

        return hasEntry;
    }

    /**
     * Safe removing a directory entails updating every other directory
     * within the list to update with the appropriate unique name that
     * corresponds to the position in which the directory can be found
     * in the list.
     *
     * After the list is updated, the directory to removed from the list
     * is then removed.
     *
     * @param dirName The name of the directory that needs to be removed.
     * @return Always returns true.
     */
    private boolean safeRemoveDir(final String dirName) {
        final int rmvDirIndex = Integer.parseInt(
                dirName.substring(
                        dirName.lastIndexOf('#') + 1
                )
        );
        final Map<String, DirectoryTree<K>> treeCopy;
        treeCopy = new HashMap<>(this.navTree);
        treeCopy.remove(dirName);

        for (int x = 0; x < this.directoryLeaves.size(); x++) {
            String oldKey = this.directoryLeaves.get(x).dirUUID();
            int i = Integer.parseInt(
                    oldKey.substring(
                            oldKey.lastIndexOf('#') + 1
                    )
            );

            if (i > rmvDirIndex) {
                int newIndex = i - 1;
                String newKey = oldKey.substring(
                        0, oldKey.lastIndexOf('#') + 1
                ) + newIndex;
                DirectoryTree<K> val = navTree.get(oldKey);

                treeCopy.remove(oldKey);
                treeCopy.put(newKey, val);
                this.directoryLeaves.get(i).setDirUUID(newKey);
                val.uniqueName = newKey;
                val.index--;
            }
        }

        this.navTree = treeCopy;
        this.directoryLeaves.remove(rmvDirIndex);

        return true;
    }

    /**
     * Removes an entry from the directory list.
     * @param indexIn the index of the entry to be removed
     * @return Returns false if the index is out of bounds
     *         Returns true if the entry was successfully deleted
     */
    public boolean removeEntry(final int indexIn) {
        boolean ret = indexIn >= 0 && indexIn < this.entries.size();
        if (ret) {
            this.entries.remove(indexIn);
        }
        return ret;
    }

    //:: Data writing
    //:::::::::::::::::::::::::::::://

    /**
     * Writes all worlds with out a proper in game directory
     * to the base in game directory.
     * @param folder The navTree location to move the worlds out of.
     * @return Returns true if worlds were successfully move.<br>
     * Returns false otherwise.
     */
    private static boolean writeWorldsToBase(
            final DirectoryTree<WorldEntry> folder
    ) {
        for (final WorldEntry entry : folder.entries) {
            File createSavePath = Minecraft.getMinecraft()
                    .getSaveLoader()
                    .getFile(
                            entry.getWorldFileName(),
                            MOPMLiterals.MOPM_SAVE_DAT
                    );
            createSavePath.getParentFile().mkdirs();

            if (!writeWorldToBase(createSavePath)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param worldFolder the file in which the world save path location
     *                    is saved in.
     * @return Returns true if the world was successfully written to the
     * base directory. false otherwise.
     */
    public static boolean writeWorldToBase(final File worldFolder) {
        try (
                DataOutputStream write =
                        new DataOutputStream(
                                new FileOutputStream(worldFolder)
                        )
        ) {
            write.write(MOPMLiterals.BASE_DIR.getBytes());
        } catch (IOException e) {
            References.LOG.error(e);
            return false;
        }
        return true;
    }

    /**
     * @return Returns true if all worlds were successfully saved to the
     * base directory. returns false if only one world was unsuccessfully
     * saved.
     * @throws AnvilConverterException Thrown if worlds couldn't be loaded.
     */
    private static boolean safeWriteWorldsToBase()
            throws AnvilConverterException {
        final Minecraft mc = Minecraft.getMinecraft();
        final ISaveFormat saveLoader = mc.getSaveLoader();

        for (final WorldSummary summary : saveLoader.getSaveList()) {
            final File mopmSaveFile = saveLoader.getFile(
                    summary.getFileName(),
                    MOPMLiterals.MOPM_SAVE_DAT
            );
            if (!writeWorldToBase(mopmSaveFile)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Ensures that all worlds on disk have an in game directory
     * location.
     * @return Returns true if all worlds have a location in game. <br>
     * Returns false if this errors at all.
     */
    private static boolean ensureMopmWorldSaveFile() {
        final Minecraft mc = Minecraft.getMinecraft();
        final ISaveFormat saveLoader = mc.getSaveLoader();

        try {
            for (final WorldSummary summary : saveLoader.getSaveList()) {
                final File mopmSaveFile = saveLoader.getFile(
                        summary.getFileName(),
                        MOPMLiterals.MOPM_SAVE_DAT
                );
                if (!mopmSaveFile.exists() && !writeWorldToBase(mopmSaveFile)) {
                    return false;
                }
            }
            return true;
        } catch (AnvilConverterException e) {
            References.LOG.error("", e);
            return false;
        }
    }

    /**
     * @param folder the virtual folder servers are contained in
     * @return returns true if all servers were successfully saved to the
     * base directory. returns false if only one server was unsuccessfully
     * saved.
     */
    private static boolean writeServersToBase(
            final DirectoryTree<ServerEntry> folder
    ) {
        for (final ServerEntry entry : new ArrayList<>(folder.entries)) {
            writeServerToBase(entry);
        }
        return true;
    }

    /**
     * Writes an individual server entry's location to the base directory.
     * @param entry The entry to write to the base directory.
     * @return Returns true always.
     */
    public static boolean writeServerToBase(final ServerEntry entry) {
        entry.setUniquePath(MOPMLiterals.BASE_DIR);
        entry.updateList();
        return true;
    }

    /**
     * Writes all servers to to the base directory.
     * @return Returns true if all servers were written properly.<br>
     * Returns false if this errors out.
     */
    private static boolean safeWriteServersToBase() {
        try {
            NBTTagCompound nbtCompound =
                    CompressedStreamTools.read(ServerSaveLoadUtils.SAVE_DIR);
            if (nbtCompound == null) {
                return false;
            }
            NBTTagList tagList = nbtCompound.getTagList(
                    MOPMLiterals.SERVERS_TAG,
                    ServerSaveLoadUtils.SAVE_TYPE
            );

            for (int i = 0; i < tagList.tagCount(); ++i) {
                final NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
                tagCompound.setString(
                        MOPMLiterals.MOPM_SAVE,
                        MOPMLiterals.BASE_DIR
                );
            }
        } catch (IOException e) {
            References.LOG.error(e);
            return false;
        }
        return true;
    }

    /**
     * Determines how the load file should be loaded.
     * @param loadFrom the file to load from.
     */
    public final void load(final File loadFrom) {
        if (!loadFrom.isFile()) {
            hardLoad(loadFrom);
        } else {
            this.softLoad(loadFrom);
        }
    }

    /**
     * Resets the load file to a default state.
     * @param loadFrom The corrupted/un-existent mopm data file.
     */
    private static void hardLoad(final File loadFrom) {
        try (
                DataOutputStream write =
                        new DataOutputStream(
                                new FileOutputStream(loadFrom)
                        )
        ) {
            write.write((MOPMLiterals.BASE_DIR + ":").getBytes());

            final String fileName = loadFrom.getName();
            if (fileName.equals(MOPMLiterals.MOPM_SSP)) {
                safeWriteWorldsToBase();
            } else if (fileName.equals(MOPMLiterals.MOPM_SMP)) {
                safeWriteServersToBase();
            } else {
                throw new FileNotFoundException();
            }
        } catch (IOException | AnvilConverterException e) {
            References.LOG.error("", e);
        }
    }

    /**
     * Loads in the the mopm save data from the load file.
     * @param loadFrom The mopm save file that contains all the load data.
     */
    public final void softLoad(final File loadFrom) {
        ensureMopmWorldSaveFile();
        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(loadFrom)
                        )
        ) {
            if (!reader.readLine().equals(MOPMLiterals.BASE_DIR + ":")) {
                throw new IOException();
            }

            Deque<DirectoryTree<K>> loadOrder = new ArrayDeque<>();
            loadOrder.push(this);
            String line;
            while ((line = reader.readLine()) != null) {
                DirectoryTree<K> top = loadOrder.peek();
                int lineDepth = 0;
                for (
                        char c = line.charAt(lineDepth);
                        c == '\t';
                        c = line.charAt(lineDepth)
                ) {
                    lineDepth++;
                }

                String directoryName = line.substring(
                        lineDepth,
                        line.lastIndexOf('#')
                );
                if (top.depth >= lineDepth) {
                    while (loadOrder.size() > lineDepth) {
                        loadOrder.pop();
                    }
                    top = loadOrder.peek();
                }
                loadOrder.push(
                        top.newFolder(
                                directoryName.replaceAll(
                                        "/",
                                        "_")
                        )
                );
            }
        } catch (IOException | NoSuchElementException e) {
            hardLoad(loadFrom);
            References.LOG.error("", e);
        }
    }

    /**
     * @param saveTo the file to save to.
     * @return Returns true if the file was successfully saved.<br>
     * Returns false otherwise.
     */
    public boolean save(final File saveTo) {
        //:: Return false if the directory is not the base directory.
        if (this.depth != 0) {
            return false;
        }

        try (
                DataOutputStream writer =
                        new DataOutputStream(
                                new FileOutputStream(saveTo)
                        )
        ) {
            writer.write(listDirectories(false).getBytes());
        } catch (IOException e) {
            References.LOG.error("Unable to save: ", e);
            return false;
        }

        return true;
    }

    //--------------------------------------------------//

    //-----Overridden Operations:-----------------------//

    /**
     * @return A String representing the directory tree.
     */
    @Override
    public String toString() {
        return listDirectories(true);
    }
}
