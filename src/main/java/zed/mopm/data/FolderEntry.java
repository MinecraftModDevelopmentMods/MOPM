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
    public FolderEntry folderPath(String path) throws NoSuchElementException {
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
     *
     * @return
     */
    public String displayName() {
        return this.name;
    }

    /**
     *
     * @return
     */
    public String getUniqueName() {
        return this.uniqueName;
    }

    /**
     *
     * @return
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     *
     * @return
     */
    public int folders() {
        return folders.size();
    }

    /**
     *
     * @return
     */
    public int entries() {
        return entries.size();
    }

    /**
     *
     * @return
     */
    public int size() {
        return folders.size() + entries.size();
    }

    //-----------------------------------------------------------------------------//

    //:: Actions
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: Modifying the directory
    //:::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @param index
     * @param name
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
     *
     * @param index
     * @return
     */
    public boolean removeDir(int index) {
        return removeDir(this.dirs.get(index).uniqueDirName);
    }

    /**
     *
     * @param dirName
     * @return
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
     *
     * @param dirName
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
    private boolean writeWorldsToBase(final FolderEntry folder) {
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
    private boolean writeWorldToBase(final File worldFolder) {
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
    private boolean safeWriteWorldsToBase() throws AnvilConverterException {
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
    private boolean writeServersToBase(final FolderEntry folder) {
        return true;
    }

    private boolean safeWriteServersToBase() {
        return true;
    }

    /**
     *
     * @param loadFrom
     */
    public void load(File loadFrom) {
        if (!loadFrom.isFile()) {
            try (DataOutputStream write = new DataOutputStream(new FileOutputStream(loadFrom))) {
                write.write("base#0".getBytes());

                final String fileName = loadFrom.getName();
                if (fileName.equals("mopm_ssp.dat")) {
                    safeWriteWorldsToBase();
                }
                else {
                    safeWriteServersToBase();
                }
            }
            catch (IOException | AnvilConverterException e) {
                References.LOG.error("", e);
            }
        }
        else {

        }
    }

    /**
     *
     */
    public void softLoad() {
        //Todo:: Work on. Soft sorts worlds into the folders.
    }

    //-----------------------------------------------------------------------------//

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Operations:---------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        StringBuilder dep = new StringBuilder();
        for (int i = 0; i < this.depth; i++) {
            dep.append("\t");
        }

        str.append("\n").append(dep).append(this.uniqueName).append(":");

        for (Directory dir : this.dirs) {
            String key = dir.dirUUID();
            str.append(dep).append(this.folders.get(key).toString());
            for (K entry : this.folders.get(key).entries) {
                str.append("\n\t").append(dep).append("- ").append(entry.toString());
            }
        }

        return str.toString();
    }
}