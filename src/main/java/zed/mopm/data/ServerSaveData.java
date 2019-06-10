package zed.mopm.data;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import zed.mopm.api.data.ServerDataStatus;
import zed.mopm.util.MOPMLiterals;

public class ServerSaveData {

    //-----Fields:--------------------------------------//

    /**
     * Defines the actions that are being enacted on the
     * server save data.
     */
    private ServerDataStatus status = ServerDataStatus.NONE;
    /**
     * Contains the server save data such as the ip and the
     * name of the server.
     */
    private ServerData saveData;
    /**
     * The location where the containing server entry is located.
     */
    private String savePath;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new mopm server save data with the given vanilla
     * save data and the containing save path in the directory list.
     * @param data The vanilla server data.
     * @param path The directory list location of the server entry.
     */
    public ServerSaveData(final ServerData data, final String path) {
        this.saveData = data;
        this.savePath = path;
    }

    /**
     * Makes a copy of another mopm server save data.
     * @param copyFrom The server save data to copy from.
     */
    public ServerSaveData(final ServerSaveData copyFrom) {
        this.saveData = new ServerData("", "", false);
        this.saveData.copyFrom(copyFrom.saveData);
        this.savePath = copyFrom.savePath;
    }

    /**
     * Creates a default server save data with no name, no ip, and no
     * directory path.
     */
    public ServerSaveData() {
        this.saveData = new ServerData("", "", false);
        this.savePath = "";
    }

    //-----This:----------------------------------------//

    /**
     * @return Returns the vanilla save data that contains the server
     * ip and name.
     */
    public final ServerData getServerData() {
        return this.saveData;
    }

    /**
     * @return Returns the containing directory of the associated server entry.
     */
    public final String getSavePath() {
        return this.savePath;
    }

    /**
     * @return Returns the current action being enacted on the server save data.
     */
    public final ServerDataStatus getStatus() {
        return this.status;
    }

    /**
     * @return Returns the save nbt of the server save data.
     */
    public final NBTTagCompound getNBTSaveData() {
        final NBTTagCompound nbt = this.saveData.getNBTCompound();
        nbt.setTag(MOPMLiterals.MOPM_SAVE, new NBTTagString(this.savePath));
        return nbt;
    }

    /**
     * Sets the directory path of where the server entry is located.
     * @param path The new path location.
     */
    public final void setSavePath(final String path) {
        this.savePath = path;
    }

    /**
     * Changes the status of the current action being enacted on the
     * server save data.
     * @param dataStatus The new status.
     */
    public final void changeStatus(final ServerDataStatus dataStatus) {
        this.status = dataStatus;
    }

    /**
     * Copies the information of another server save data onto this
     * server save data.
     * @param newSave The server save data to copy from.
     */
    public final void copyFrom(final ServerSaveData newSave) {
        this.savePath = newSave.savePath;
        this.saveData.copyFrom(newSave.getServerData());
        this.saveData.serverMOTD = newSave.getServerData().serverMOTD;
    }
}
