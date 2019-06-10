package zed.mopm.api.gui.lists;

public interface IModifiableList {
    void rename(int entryIndex, String name);
    void delete(int entryIndex);
    void changeDir(int entryIndex);
}
