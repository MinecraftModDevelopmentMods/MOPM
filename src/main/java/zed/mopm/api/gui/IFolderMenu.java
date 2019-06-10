package zed.mopm.api.gui;

import zed.mopm.gui.elements.lists.DirectoryList;

public interface IFolderMenu {
    DirectoryList getDirectoryList();
    void refreshDirectoryEntryList();
}
