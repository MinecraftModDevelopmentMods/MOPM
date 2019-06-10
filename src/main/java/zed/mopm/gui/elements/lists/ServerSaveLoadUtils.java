package zed.mopm.gui.elements.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.ServerSaveData;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.client.multiplayer.ServerData.getServerDataFromNBTCompound;

public class ServerSaveLoadUtils
        extends ServerList
        implements Iterable<ServerSaveData> {

    //-----Consts:--------------------------------------//

    /**
     * The NBT save type of the servers.dat file.
     */
    public static final int SAVE_TYPE = 10;
    /**
     * A reference to the servers.dat file.
     */
    public static final File SAVE_DIR =
            new File(
                    Minecraft.getMinecraft().gameDir,
                    MOPMLiterals.SERVERS_DAT
            );

    /**
     * The error message if the servers.dat file could not be loaded.
     */
    private static final String LOAD_ERR = "Couldn't load server list!"
            + "Try closing all programs that may be in use of the "
            + "servers.dat file.";
    /**
     * The error message if the servers.dat file could not be saved.
     */
    private static final String SAVE_ERR = "Failed to save server list! "
            + "servers.dat_tmp has been generated as a backup. Please reload "
            + "the game with the back up and close all programs that might be "
            + "in use of the servers.dat file";
    /**
     * The error message if the servers.dat file could not be written to.
     */
    private static final String CREATE_FILE_ERR = "Failed to save server.dat";

    //-----Fields:--------------------------------------//

    /**
     * The list of server save data that has been loaded.
     */
    private List<ServerSaveData> servers;

    //-----Constructors:--------------------------------//

    /**
     * Creates a server save load utils instance that can be used
     * to load in servers from the servers.dat file.
     * @param mcIn The minecraft client.
     */
    public ServerSaveLoadUtils(final Minecraft mcIn) {
        super(mcIn);
        this.createDataFile();
    }

    //-----Overridden Methods:--------------------------//

    //:: ServerList
    //:::::::::::::::::::::::::::::://

    /**
     * Loads the list of saved servers from the servers.dat file.
     */
    @Override
    public final void loadServerList() {
        try {
            if (servers != null) {
                this.servers.clear();
            } else {
                this.servers = new ArrayList<>();
            }

            NBTTagCompound nbtCompound = CompressedStreamTools.read(SAVE_DIR);
            if (nbtCompound == null) {
                return;
            }
            NBTTagList tagList = nbtCompound.getTagList(
                    MOPMLiterals.SERVERS_TAG, SAVE_TYPE
            );

            for (int i = 0; i < tagList.tagCount(); ++i) {
                final NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
                if (!tagCompound.hasKey(MOPMLiterals.MOPM_SAVE)) {
                    tagCompound.setString(MOPMLiterals.MOPM_SAVE, "");
                }
                this.servers.add(
                        new ServerSaveData(
                                getServerDataFromNBTCompound(tagCompound),
                                tagCompound.getString(MOPMLiterals.MOPM_SAVE)
                        )
                );
            }
        } catch (Exception exception) {
            References.LOG.error(LOAD_ERR, exception);
        }
    }

    /**
     * Swaps the position of servers within the server list.
     * @param pos1 Position of the first server to be swapped.
     * @param pos2 The new position of the first server.
     */
    @Override
    public final void swapServers(final int pos1, final int pos2) {
        final ServerSaveData swap = getDetailAt(pos1);
        this.servers.set(pos1, this.servers.get(pos2));
        this.servers.set(pos2, swap);
        this.save();
    }

    //:: Iterable
    //:::::::::::::::::::::::::::::://

    /**
     * @return Returns the server list iterator.
     */
    @Override
    public final Iterator<ServerSaveData> iterator() {
        return this.servers.iterator();
    }

    //-----This:----------------------------------------//

    /**
     * Saves the list of servers to the servers.dat file.
     */
    public final void save() {
        try {
            final NBTTagList writeList = new NBTTagList();
            for (final ServerSaveData data : servers) {
                writeList.appendTag(data.getNBTSaveData());
            }
            final NBTTagCompound write = new NBTTagCompound();
            write.setTag(MOPMLiterals.SERVERS_TAG, writeList);
            CompressedStreamTools.safeWrite(write, SAVE_DIR);
        } catch (IOException e) {
            References.LOG.error(SAVE_ERR, e);
        }
    }

    /**
     * Adds a server save data to the save list.
     * @param data The data to be added to the save list.
     */
    public final void addSaveData(final ServerSaveData data) {
        final ServerSaveData newData = new ServerSaveData(data);
        servers.add(newData);
    }

    /**
     * Removes server save data from the save data list.
     * @param index The index of the server save data to be removed.
     */
    public final void removeSaveData(final int index) {
        this.servers.remove(index);
    }

    /**
     * Replaces save data in the list with new save data information.
     * @param replaceAt The index of the save data to replace.
     * @param data The new save data.
     */
    public final void replace(final int replaceAt, final ServerSaveData data) {
        this.servers.get(replaceAt).copyFrom(data);
    }

    /**
     * @param listScreen The server selection menu.
     * @return Returns the list of save data.
     */
    public final List<ServerEntry> getDetails(final GuiMultiplayer listScreen) {
        final List<ServerEntry> loadInServer = new ArrayList<>();
        int i = 0;
        for (final ServerSaveData data : this.servers) {
            loadInServer.add(new ServerEntry(listScreen, data, i));
            i++;
        }
        return loadInServer;
    }

    /**
     * Gets server save data at a provided index.
     * @param index The index of the server save data list to get.
     * @return Returns the server save data at the given index.
     */
    public final ServerSaveData getDetailAt(final int index) {
        return this.servers.get(index);
    }

    /**
     * Creates a save data file if one does not exist.
     */
    private void createDataFile() {
        if (!SAVE_DIR.exists()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag(MOPMLiterals.SERVERS_TAG, new NBTTagList());
            try {
                CompressedStreamTools.safeWrite(tag, SAVE_DIR);
            } catch (IOException e) {
                References.LOG.error(CREATE_FILE_ERR, e);
            }
        }
    }
}
