package zed.mopm.data;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import zed.mopm.api.data.ServerDataStatus;
import zed.mopm.util.MOPMLiterals;

public class ServerSaveData {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private ServerDataStatus status = ServerDataStatus.NONE;
    private ServerData data;
    private String savePath;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public ServerSaveData(final ServerData data, final String savePath) {
        this.data = data;
        this.savePath = savePath;
    }

    public ServerSaveData(final ServerSaveData copyFrom) {
        this.data = new ServerData("", "", false);
        this.data.copyFrom(copyFrom.data);
        this.savePath = copyFrom.savePath;
    }

    public ServerSaveData() {
        this.data = new ServerData("", "", false);
        this.savePath = "";
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public ServerData getServerData() {
        return this.data;
    }

    public String getSavePath() {
        return this.savePath;
    }

    public ServerDataStatus getStatus() {
        return this.status;
    }

    public NBTTagCompound getNBTSaveData() {
        final NBTTagCompound nbt = this.data.getNBTCompound();
        nbt.setTag(MOPMLiterals.MOPM_SAVE, new NBTTagString(this.savePath));
        return nbt;
    }

    public void setSavePath(final String path) {
        this.savePath = path;
    }

    public void changeStatus(ServerDataStatus status) {
        this.status = status;
    }

    public void copyFrom(final ServerSaveData newSave) {
        this.savePath = newSave.savePath;
        this.data.copyFrom(newSave.getServerData());
        this.data.serverMOTD = newSave.getServerData().serverMOTD;
    }
}
