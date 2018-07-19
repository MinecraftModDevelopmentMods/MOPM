package zed.mopm.api.gui;

import zed.mopm.gui.lists.FolderList;

public interface IFolderMenu {
    FolderList getDirectoryList();
    void refreshDirectoryEntryList();
}
