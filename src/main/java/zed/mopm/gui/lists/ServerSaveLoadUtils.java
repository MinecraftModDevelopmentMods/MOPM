package zed.mopm.gui.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
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

public class ServerSaveLoadUtils extends ServerList implements Iterable<ServerSaveData> {
    private static final File SAVE_DIR = new File(Minecraft.getMinecraft().gameDir, "servers.dat");
    private List<ServerSaveData> servers;
    public ServerSaveLoadUtils(Minecraft mcIn) {
        super(mcIn);
        this.createDataFile();
    }

    @Override
    public void loadServerList() {
        try {
            if (servers != null) {
                servers.clear();
            }
            else {
                this.servers = new ArrayList<>();
            }

            NBTTagCompound nbtCompound = CompressedStreamTools.read(SAVE_DIR);
            if (nbtCompound == null) {
                return;
            }
            NBTTagList tagList = nbtCompound.getTagList("servers", 10);

            for (int i = 0; i < tagList.tagCount(); ++i) {
                final NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
                if (!tagCompound.hasKey(MOPMLiterals.MOPM_SAVE)) {
                    tagCompound.setString(MOPMLiterals.MOPM_SAVE, "");
                }
                this.servers.add(new ServerSaveData(ServerData.getServerDataFromNBTCompound(tagCompound), tagCompound.getString(MOPMLiterals.MOPM_SAVE)));
            }
        }
        catch (Exception exception) {
            References.LOG.error("Couldn't load server list! Try closing all programs that may be in use of the servers.dat file.", exception);
        }
    }

    public void save() {
        try {
            final NBTTagList writeList = new NBTTagList();
            for (final ServerSaveData data : servers) {
                writeList.appendTag(data.getNBTSaveData());
            }
            final NBTTagCompound write = new NBTTagCompound();
            write.setTag("servers", writeList);
            CompressedStreamTools.safeWrite(write, SAVE_DIR);
        } catch (IOException e) {
            References.LOG.error("Failed to save server list! servers.dat_tmp has been generated as a backup. Please reload the game with the back up and close all programs that might be in use of the servers.dat file", e);
        }
    }

    public void addSaveData(final ServerSaveData data) {
        final ServerSaveData newData = new ServerSaveData(data);
        servers.add(newData);
        super.addServerData(newData.getServerData());
    }

    public void removeSaveData(final int index) {
        this.servers.remove(index);
    }

    @Override
    public Iterator<ServerSaveData> iterator() {
        return this.servers.iterator();
    }

    public List<ServerEntry> getDetails(final GuiMultiplayer listScreen) {
        final List<ServerEntry> loadInServer = new ArrayList<>();
        int i = 0;
        for (final ServerSaveData data : this.servers) {
            loadInServer.add(new ServerEntry(listScreen, data, i));
            i++;
        }
        return loadInServer;
    }

    private void createDataFile() {
        if (!SAVE_DIR.exists()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("servers", new NBTTagList());
            try {
                CompressedStreamTools.safeWrite(tag, SAVE_DIR);
            } catch (IOException e) {
                References.LOG.error("Failed to save server.dat", e);
            }
        }
    }
}
