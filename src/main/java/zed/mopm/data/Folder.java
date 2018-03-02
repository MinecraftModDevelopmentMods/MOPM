package zed.mopm.data;

import java.util.List;

public class Folder <K> {
    /**
     * K can be ServerListEntryNormal
     * K can be GuiListWorldSelectionEntry
     */
    private int depth;
    private String name;

    private List<Folder<K>> folders;
    private List<K> entries;

    public Folder(String name) {
        this.name = name;
        this.depth = 0;
    }

    private Folder (String name, int depth) {
        this(name);
        this.depth = depth;
    }

    public void newFolder(String name) {
        folders.add(new Folder(name, depth + 1));
    }

    public void newEntry() {

    }

    public String displayName() {
        return this.name;
    }

    public int getDepth() {
        return this.depth;
    }

    public int folders() {
        return folders.size();
    }

    public int entries() {
        return entries.size();
    }

    public int size() {
        return folders.size() + entries.size();
    }
}
