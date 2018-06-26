package zed.mopm.data;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.*;

public class FolderEntry<K> implements Cloneable {
    /**
     * K can be ServerListEntryNormal
     * K can be GuiListWorldSelectionEntry
     */
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
    public FolderEntry stepDown(int index) throws IndexOutOfBoundsException {
        return folders.get(this.dirs.get(index).dirUUID());
    }

    public FolderEntry stepDown(String name) {
        return folders.get(name);
    }

    /**
     * folder1/folder2/folder3/filename.end
     *
     * Might need a rework!
     * @param path
     * @return
     */
    public K get(String path) throws NoSuchElementException {
        FolderEntry<K> currentFolder = this;
        if (path.endsWith(".end")) {
            for (String part : path.split("/")) {
                if (part.endsWith(".end")) {
                    if (currentFolder == null) {
                        throw new NoSuchElementException();
                    }

                    part = part.substring(0, part.indexOf("."));
                    for (Object toReturn : currentFolder.entries) {
                        if (toReturn.toString().equals(part.toString()) && toReturn.hashCode() == part.hashCode()) {
                            return (K)toReturn;
                        }
                    }
                }

                if (currentFolder.folders.containsKey(part)) {
                    currentFolder = currentFolder.folders.get(part);
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        }
        throw new NoSuchElementException();
    }

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

    public Directory getDirectory(int i) {
        try {
            return dirs.get(i);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public K getEntry(int i) {
        return this.entries.get(i);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Utilities:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @return
     */
    public String displayName() {
        return this.name;
    }

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

    public void load(File loadFrom) {
        try {
            if (!loadFrom.isFile()) {
                    DataOutputStream write = new DataOutputStream(new FileOutputStream(loadFrom));
                    write.write("base#0".getBytes());
                    write.close();
            }
            else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Operations:---------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public String toString() {
        String str = "";
        String dep = "";
        for (int i = 0; i < this.depth; i++) {
            dep += "\t";
        }

        str += "\n" + (dep + this.uniqueName + ":");

        for (Directory dir : this.dirs) {
            String key = dir.dirUUID();
            str += (dep + this.folders.get(key).toString());
            for (K entry : this.folders.get(key).entries) {
                str += ("\n\t" + dep + "- " + entry.toString());
            }
        }

        return str;
    }

    @Override
    public FolderEntry clone() {
        FolderEntry<K> cloned;
        if (this.depth == 0) {
            cloned = new FolderEntry("base");
        }
        else {
            cloned = new FolderEntry(this.name, this.depth, this.index);
        }

        cloned.entries = Lists.newArrayList(this.entries);
        cloned.dirs = Lists.newArrayList(this.dirs);

        for (String key : this.folders.keySet()) {
            cloned.folders.put(key, this.folders.get(key).clone());
        }

        return cloned;
    }
}