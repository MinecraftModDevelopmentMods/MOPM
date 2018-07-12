package zed.mopm.api.data;

import zed.mopm.gui.lists.FolderList;

public interface IFolderMenu {
    FolderList getDirectoryList();
    void refreshDirectoryEntryList();
}
