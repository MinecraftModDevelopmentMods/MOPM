package zed.mopm.data;

import com.google.common.collect.Lists;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.util.References;

import java.io.*;
import java.util.*;

/**
 * K can be ServerListEntryNormal
 * K can be GuiListWorldSelectionEntry
 */
public class FolderEntry<K> {
    private int depth;
    private int index;
    private String name;
    private String uniqueName;

    private List<K> entries;
    private List<Directory> dirs;
    private Map<String, FolderEntry<K>> folders;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @param name
     */
    public FolderEntry(String name) {
        folders = new HashMap<>();
        dirs = new ArrayList<>();
        entries = new ArrayList<>();

        this.name = name;
        this.uniqueName = name + "#0";
        this.depth = 0;
        this.index = 0;
    }

    /**
     *
     * @param name
     * @param depth
     */
    private FolderEntry(String name, int depth, int index) {
        this(name);
        this.uniqueName = name + "#" + index;
        this.depth = depth;
        this.index = index;
    }

    /**
     *
     * @param copyFrom
     */
    public FolderEntry(FolderEntry<K> copyFrom) {
        this(copyFrom.name, copyFrom.depth, copyFrom.index);

        this.entries = Lists.newArrayList(copyFrom.entries);
        this.dirs = Lists.newArrayList(copyFrom.dirs);

        for (String key : copyFrom.folders.keySet()) {
            this.folders.put(key, new FolderEntry(copyFrom.folders.get(key)));
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Builders:----------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @param name
     * @return
     */
    public FolderEntry newFolder(String name) {
        FolderEntry newFolder = new FolderEntry(name, depth + 1, this.folders.size());
        folders.put(newFolder.uniqueName, newFolder);
        dirs.add(new Directory(name, newFolder.uniqueName));
        return newFolder;
    }

    /**
     *
     * @param entry
     * @return
     */
    public FolderEntry newEntry(K entry) {
        entries.add(entry);
        return this;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Navigation:--------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @param index
     * @return
     */
    public FolderEntry stepDown(int index) {
        return folders.get(this.dirs.get(index).dirUUID());
    }

    /**
     *
     * @param name
     * @return
     */
    public FolderEntry stepDown(String name) {
        return folders.get(name);
    }

    /**
     *
     * @param path
     * @return
     * @throws NoSuchElementException
     */
    public FolderEntry folderPath(String path) {
        FolderEntry<K> current = this;
        for (String part : path.split("/")) {
            if (current.folders.containsKey(part)) {
                current = current.folders.get(part);
            }
            else {
                throw new NoSuchElementException();
            }
        }

        return current;
    }

    /**
     *
     * @param i
     * @return
     */
    public Directory getDirectory(int i) {
        try {
            return dirs.get(i);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     *
     * @param i
     * @return
     */
    public K getEntry(int i) {
        return this.entries.get(i);
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
        return folders.size();
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
        return folders.size() + entries.size();
    }

    private String listDirectories(boolean showEntries) {
        StringBuilder str = new StringBuilder();
        StringBuilder dep = new StringBuilder();
        for (int i = 0; i < this.depth; i++) {
            dep.append("\t");
        }

        str.append((this.depth == 0) ? "" : "\n").append(dep).append(this.uniqueName).append(":");

        for (Directory dir : this.dirs) {
            String key = dir.dirUUID();
            str.append(dep).append(this.folders.get(key).listDirectories(showEntries));
            if (showEntries) {
                for (K entry : this.folders.get(key).entries) {
                    str.append("\n\t").append(dep).append("- ").append(entry.toString());
                }
            }
        }

        return str.toString();
    }

    //-----------------------------------------------------------------------------//

    //:: Actions
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: Modifying the directory
    //:::::::::::::::::::::::::::::::::::::::::::://

    /**
     * Renames a directory in the called upon directory.
     *
     * @param index The directory index that will be renamed.
     * @param name The new name for the directory.
     */
    public void renameDir(int index, String name) {
        FolderEntry temp = this.stepDown(index);
        String oldName = temp.uniqueName;

        this.folders.remove(oldName);

        temp.name = name;
        temp.uniqueName = name + this.uniqueName.substring(this.uniqueName.lastIndexOf('#'));

        this.folders.put(temp.uniqueName, temp);

        Directory tempDir = this.dirs.get(index);
        tempDir.dirName = temp.name;
        tempDir.uniqueDirName = temp.uniqueName;

    }

    /**
     * @see #removeDir(String)
     * @param index The index used to reference what subdirectory should be removed from the called upon directory.
     * @return
     */
    public boolean removeDir(int index) {
        return removeDir(this.dirs.get(index).uniqueDirName);
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
        if (!this.folders.containsKey(dirName)) {
            return false;
        }

        final FolderEntry<K> temp = this.folders.get(dirName);
        boolean hasEntry = temp.entries.isEmpty();

        if (!hasEntry) {
            if (temp.entries.get(0) instanceof WorldEntry) {
                hasEntry = writeWorldsToBase(temp);
            }
            if (temp.entries.get(0) instanceof ServerEntry) {
                hasEntry = writeServersToBase(temp);
            }
        }

        for (final String key : temp.folders.keySet()) {
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

        for (int x = 0; x < this.dirs.size(); x++) {
            String oldKey = this.dirs.get(x).uniqueDirName;
            int i = Integer.parseInt(oldKey.substring(oldKey.lastIndexOf('#') + 1));

            if (i > rmvDirIndex) {
                String newKey = oldKey.substring(0, oldKey.lastIndexOf('#') + 1) + (i - 1);
                FolderEntry<K> val = folders.get(oldKey);

                this.folders.remove(oldKey);
                this.folders.put(newKey, val);
                this.dirs.get(i).uniqueDirName = newKey;
                val.uniqueName = newKey;
                val.index--;
            }
        }

        this.folders.remove(dirName);
        this.dirs.remove(rmvDirIndex);

        return true;
    }

    //:: Data writing
    //:::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @param folder
     * @return
     */
    private static boolean writeWorldsToBase(final FolderEntry folder) {
        for (final WorldEntry entry : (List<WorldEntry>)folder.entries) {
            File createSavePath = Minecraft.getMinecraft().getSaveLoader().getFile(entry.getFileName(), "mopm_save.dat");
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
    private static boolean writeWorldToBase(final File worldFolder) {
        try (DataOutputStream write = new DataOutputStream(new FileOutputStream(worldFolder))) {
            write.write("base#0".getBytes());
        }
        catch (IOException e) {
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
            if (!writeWorldToBase(saveLoader.getFile(summary.getFileName(), "mopm_save.dat"))) {
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
        }
        else {
            this.softLoad(loadFrom);
        }
    }

    /**
     *
     * @param loadFrom
     */
    private static void hardLoad(final File loadFrom) {
        try (DataOutputStream write = new DataOutputStream(new FileOutputStream(loadFrom))) {
            write.write("base#0:".getBytes());

            final String fileName = loadFrom.getName();
            if (fileName.equals("mopm_ssp.dat")) {
                safeWriteWorldsToBase();
            }
            else if (fileName.equals("mopm.smp.dat")){
                safeWriteServersToBase();
            }
            else {
                throw new FileNotFoundException();
            }
        }
        catch (IOException | AnvilConverterException e) {
            References.LOG.error("", e);
        }
    }

    /**
     *
     */
    public void softLoad(final File loadFrom) {
        try (BufferedReader reader = new BufferedReader(new FileReader(loadFrom))) {

            if (!reader.readLine().equals("base#0:")){
                throw new IOException();
            }

            FolderEntry<K> currentLoad = this;
            String str;
            String line;
            while ((line = reader.readLine())  != null) {
            }
        }
        catch (IOException e) {
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
        }
        catch (IOException e) {
            return false;
        }

        return true;
    }

    //-----------------------------------------------------------------------------//

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Operations:---------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     * Converts the object to a String.
     * The String will represent the branching directory structure of the object.
     *
     * @return A String of the directory tree.
     */
    @Override
    public String toString() {
        return listDirectories(true);
    }
}