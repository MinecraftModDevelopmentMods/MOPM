package zed.mopm.data;

import com.google.common.collect.Lists;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.*;
import java.util.*;

/**
 * K can be ServerListEntryNormal.
 * K can be GuiListWorldSelectionEntry.
 *
 * @param <K>
 */
public class FolderEntry<K> {
    private int depth;
    private int index;
    private String name;
    private String uniqueName;

    private List<K> entries;
    private List<Directory> directories;
    private Map<String, FolderEntry<K>> directoryData;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @param nameIn
     */
    public FolderEntry(final String nameIn) {
        directoryData = new HashMap<>();
        directories = new ArrayList<>();
        entries = new ArrayList<>();

        this.name = nameIn;
        this.uniqueName = nameIn + "#0";
        this.depth = 0;
        this.index = 0;
    }

    /**
     *
     * @param nameIn
     * @param depthIn
     */
    private FolderEntry(final String nameIn, final int depthIn, final int indexIn) {
        this(nameIn);
        this.uniqueName = nameIn + "#" + index;
        this.depth = depthIn;
        this.index = indexIn;
    }

    /**
     *
     * @param copyFrom
     */
    public FolderEntry(final FolderEntry<K> copyFrom) {
        this(copyFrom.name, copyFrom.depth, copyFrom.index);

        this.entries = Lists.newArrayList(copyFrom.entries);
        this.directories = Lists.newArrayList(copyFrom.directories);

        for (String key : copyFrom.directoryData.keySet()) {
            this.directoryData.put(key, new FolderEntry(copyFrom.directoryData.get(key)));
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Builders:----------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     * Creates a new directory in the called upon directory.
     *
     * @param nameIn The name of the new directory.
     * @return Returns the newly created directory.
     */
    public FolderEntry<K> newFolder(final String nameIn) {
        FolderEntry<K> newFolder = new FolderEntry(nameIn, depth + 1, this.directoryData.size());
        directoryData.put(newFolder.uniqueName, newFolder);
        directories.add(new Directory(nameIn, newFolder.uniqueName));
        return newFolder;
    }

    /**
     * Inserts an entry into into the called upon directory.
     *
     * @param entry The entry to be added to the directory.
     * @return Returns the directory that was called upon.
     */
    public FolderEntry<K> newEntry(final K entry) {
        if (this.entries.contains(entry)) {
            this.entries.remove(entry);
        }
        entries.add(entry);
        return this;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Navigation:--------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     * @param indexIn List location index
     * @return Returns the subdirectory located at index within the called upon directory.
     */
    public FolderEntry<K> stepDown(final int indexIn) {
        return directoryData.get(this.directories.get(indexIn).dirUUID());
    }

    /**
     * @param nameIn The directory that is being searched for.
     * @return Returns the subdirectory with the name 'name' within the called upon directory.
     */
    public FolderEntry<K> stepDown(final String nameIn) {
        return directoryData.get(nameIn);
    }

    /**
     * @param pathIn The location in the directory tree to be returned.
     * @return Returns the subdirectory defined by the end of the path.
     * @throws NoSuchElementException Thrown if the path does not exist.
     */
    public FolderEntry<K> folderPath(final String pathIn) {
        FolderEntry<K> current = this;
        if (!pathIn.isEmpty()) {
            for (String part : pathIn.split("/")) {
                if (current.directoryData.containsKey(part)) {
                    current = current.directoryData.get(part);
                } else {
                    throw new NoSuchElementException();
                }
            }
        }

        return current;
    }

    /**
     *
     * @param i
     * @return
     */
    public Directory getDirectory(final int i) {
        return directories.get(i);
    }

    /**
     * Returns the entry of the current directory located in i.
     * @param i
     * @return
     */
    public K getEntry(final int i) {
        return this.entries.get(i);
    }

    public List<K> getEntries() {
        return this.entries;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Utilities:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: Getters
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

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
     * The navigational name is what is referred to in order to distinguish between
     * two directories within the same directory that have the same name.
     *
     * For instance:
     * The base directory may contain directories with the names of "Directory 1" and "Directory 1".
     * In order to distinguish between the two, the unique name always has a '#' with a number append
     * to the end of it.
     * Therefore:
     * base#0
     * contains:
     * Directory 1#0
     * and
     * Directory 2#1
     *
     * The corresponding numbers are the index in which they appear in the list of directories.
     *
     * @return Returns the unique name of the directory.
     */
    public String getUniqueName() {
        return this.uniqueName;
    }

    /**
     * The base directory has a depth of 0.
     * Every subdirectory within one another have an incremented depth.
     * Therefore directories within the base directory will have a depth of 1.
     *
     * Visually represented:
     * path: base/directory1/directory2/directory3
     *       0   :depth 1   :depth 2   :depth 3
     *
     * This method will return the placement in the path of the called upon directory.
     *
     * @return Returns the subdirectory count.
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     * @return Returns the number of directories contained in the called upon directory.
     */
    public int folders() {
        return directoryData.size();
    }

    /**
     * @return Returns the number of entries contained within the called upon directory.
     */
    public int entries() {
        return entries.size();
    }

    /**
     * @return Returns the total amount of directories and entries contained within the called upon directory.
     */
    public int size() {
        return directoryData.size() + entries.size();
    }

    /**
     * Converts the called upon directory object to a String.
     * The String will represent the branching directory structure of the object starting at the called
     * upon directory as the root.
     *
     * @param showEntries True: append directories's entries to the String
     *                    False: do not append entries to the String
     * @return Returns the String representation of the branching directory structure.
     */
    private String listDirectories(final boolean showEntries) {
        StringBuilder str = new StringBuilder();
        this.listDirectories(showEntries, "", str);
        return str.toString();
    }

    /**
     * @see #listDirectories(boolean)
     * @param showEntries True: append directories's entries to the String
     *                    False: do not append entries to the String
     * @param depthIn Current folder depth.
     * @param str The String to append to.
     */
    private void listDirectories(final boolean showEntries, final String depthIn, final StringBuilder str) {
        str.append(depth).append(this.uniqueName).append(':').append('\n');
        for (Directory dir : this.directories) {
            this.directoryData.get(dir.dirUUID()).listDirectories(showEntries, depthIn + "\t", str);
        }
        if (showEntries) {
            for (K entry : this.entries) {
                str.append(depth).append("- ").append(entry.toString()).append('\n');
            }
        }
    }

    //-----------------------------------------------------------------------------//

    //:: Actions
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: Modifying the directory
    //:::::::::::::::::::::::::::::::::::::::::::://

    /**
     * Renames a directory in the called upon directory.
     *
     * @param indexIn The directory index that will be renamed.
     * @param nameIn The new name for the directory.
     */
    public void renameDir(final int indexIn, final String nameIn) {
        FolderEntry<K> temp = this.stepDown(indexIn);
        String oldName = temp.uniqueName;

        this.directoryData.remove(oldName);

        temp.name = nameIn;
        temp.uniqueName = nameIn + this.uniqueName.substring(this.uniqueName.lastIndexOf('#'));

        this.directoryData.put(temp.uniqueName, temp);

        Directory tempDir = this.directories.get(indexIn);
        tempDir.dirName = temp.name;
        tempDir.uniqueDirName = temp.uniqueName;

    }

    /**
     * @see #removeDir(String)
     * @param indexIn The index used to reference what subdirectory should be removed from the called upon directory.
     * @return
     */
    public boolean removeDir(final int indexIn) {
        return removeDir(this.directories.get(indexIn).uniqueDirName);
    }

    /**
     * Attempts to remove a subdirectory from the called upon containing directory.
     * A check for worlds/servers within the subdirectory or any subdirectories of the subdirectory
     * (and onwards to the end of every branch of the directory tree)is made, and will move
     * any containing worlds/servers within any of the containing subdirectories to the immutable base directory.
     * This is to prevent the loss of world visually not showing within any of the compatible menus.
     *
     * @see #safeRemoveDir(String) This method is what does the actual remove of the subdirectory.
     *                             removeDir() does all the necessairy checks before a subdirectory
     *                             can be safely removed.
     *
     * @param dirName The subdirectory's name that will be removed from the containing directory.
     *                The containing directory is the directory that is being called upon.
     * @return Returns true if the directory was successful deleted.
     *         Returns false if there was an issue when attempting to remove a subdirectory.
     */
    public boolean removeDir(final String dirName) {
        if (!this.directoryData.containsKey(dirName)) {
            return false;
        }

        final FolderEntry<K> temp = this.directoryData.get(dirName);
        boolean hasEntry = temp.entries.isEmpty();

        if (!hasEntry) {
            if (temp.entries.get(0) instanceof WorldEntry) {
                hasEntry = writeWorldsToBase(temp);
            }
            if (temp.entries.get(0) instanceof ServerEntry) {
                hasEntry = writeServersToBase(temp);
            }
        }

        for (final String key : temp.directoryData.keySet()) {
            hasEntry = removeDir(key);
        }

        safeRemoveDir(dirName);

        return hasEntry;
    }

    /**
     * Safe removing a directory entails updating every other directory within the list to update
     * with the appropriate unique name that corresponds to the position in which the directory
     * can be found in the list.
     *
     * After the list is updated, the directory to removed from the list is then removed.
     *
     * @param dirName The name of the directory that needs to be removed.
     * @return
     */
    private boolean safeRemoveDir(final String dirName) {
        final int rmvDirIndex = Integer.parseInt(dirName.substring(dirName.lastIndexOf('#') + 1));

        for (int x = 0; x < this.directories.size(); x++) {
            String oldKey = this.directories.get(x).uniqueDirName;
            int i = Integer.parseInt(oldKey.substring(oldKey.lastIndexOf('#') + 1));

            if (i > rmvDirIndex) {
                String newKey = oldKey.substring(0, oldKey.lastIndexOf('#') + 1) + (i - 1);
                FolderEntry<K> val = directoryData.get(oldKey);

                this.directoryData.remove(oldKey);
                this.directoryData.put(newKey, val);
                this.directories.get(i).uniqueDirName = newKey;
                val.uniqueName = newKey;
                val.index--;
            }
        }

        this.directoryData.remove(dirName);
        this.directories.remove(rmvDirIndex);

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
    //:::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @param folder
     * @return
     */
    private static boolean writeWorldsToBase(final FolderEntry folder) {
        for (final WorldEntry entry : (List<WorldEntry>) folder.entries) {
            File createSavePath = Minecraft.getMinecraft().getSaveLoader().getFile(entry.getFileName(), MOPMLiterals.MOPM_SAVE);
            createSavePath.getParentFile().mkdirs();

            if (!writeWorldToBase(createSavePath)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param worldFolder
     * @return
     */
    public static boolean writeWorldToBase(final File worldFolder) {
        try (DataOutputStream write = new DataOutputStream(new FileOutputStream(worldFolder))) {
            write.write(MOPMLiterals.BASE_DIR.getBytes());
        } catch (IOException e) {
            References.LOG.error(e);
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    private static boolean safeWriteWorldsToBase() throws AnvilConverterException {
        final Minecraft mc = Minecraft.getMinecraft();
        final ISaveFormat saveLoader = mc.getSaveLoader();

        for (final WorldSummary summary : saveLoader.getSaveList()) {
            if (!writeWorldToBase(saveLoader.getFile(summary.getFileName(), MOPMLiterals.MOPM_SAVE))) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param folder
     * @return
     */
    private static boolean writeServersToBase(final FolderEntry folder) {
        //TODO: Write this for server entries
        return true;
    }

    private static boolean safeWriteServersToBase() {
        return true;
    }

    /**
     *
     * @param loadFrom
     */
    public void load(final File loadFrom) {
        if (!loadFrom.isFile()) {
            hardLoad(loadFrom);
        } else {
            this.softLoad(loadFrom);
        }
    }

    /**
     *
     * @param loadFrom
     */
    private static void hardLoad(final File loadFrom) {
        try (DataOutputStream write = new DataOutputStream(new FileOutputStream(loadFrom))) {
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
     *
     */
    public void softLoad(final File loadFrom) {
        try (BufferedReader reader = new BufferedReader(new FileReader(loadFrom))) {

            if (!reader.readLine().equals(MOPMLiterals.BASE_DIR + ":")) {
                throw new IOException();
            }

            Deque<FolderEntry<K>> loadOrder = new ArrayDeque<>();
            loadOrder.push(this);
            String line;
            while ((line = reader.readLine()) != null) {
                FolderEntry<K> top = loadOrder.peek();
                int lineDepth = 0;
                for (char c = line.charAt(lineDepth); c == '\t'; c = line.charAt(lineDepth)) {
                    lineDepth++;
                }

                String directoryName = line.substring(lineDepth, line.lastIndexOf('#'));
                if (top.depth >= lineDepth) {
                    while (loadOrder.size() > lineDepth) {
                        loadOrder.pop();
                    }
                    top = loadOrder.peek();
                }
                loadOrder.push(top.newFolder(directoryName));
            }
        } catch (IOException | NoSuchElementException e) {
            hardLoad(loadFrom);
            References.LOG.error("", e);
        }
    }

    /**
     *
     * @param saveTo
     * @return
     */
    public boolean save(final File saveTo) {
        //:: Return false if the directory is not the base directory.
        if (this.depth != 0) {
            return false;
        }

        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(saveTo))) {
            writer.write(listDirectories(false).getBytes());
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    //-----------------------------------------------------------------------------//

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Operations:---------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     * @return A String representing the directory tree.
     */
    @Override
    public String toString() {
        return listDirectories(true);
    }
}
