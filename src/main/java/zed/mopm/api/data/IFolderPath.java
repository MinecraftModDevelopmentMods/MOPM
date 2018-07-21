package zed.mopm.api.data;

import java.io.File;

public interface IFolderPath {
    void setPath(String path);

    void setUniquePath(String path);

    String getPathToDir();

    File getMopmSaveData();
}
