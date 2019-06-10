package zed.mopm.gui.menus.base;

import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import zed.mopm.api.data.ServerDataStatus;
import zed.mopm.api.gui.IMenuType;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.ServerSaveData;
import zed.mopm.gui.elements.lists.DirectoryList;
import zed.mopm.gui.elements.lists.ServerList;
import zed.mopm.gui.elements.lists.ServerSaveLoadUtils;
import zed.mopm.gui.menus.mutators.entries.CreateEntryMenu;
import zed.mopm.gui.menus.mutators.entries.CreateServerMenu;

import java.util.Arrays;
import java.util.List;

public class ServerSelectMenu
        extends GuiMultiplayer
        implements IMenuType {

    //-----Constants:-----------------------------------//

    /**
     * The id of the cancel button.
     */
    private static final int CANCEL_ID = 0;
    /**
     * The id of the join server button.
     */
    private static final int JOIN_ID = 1;
    /**
     * The id of the delete server button.
     */
    private static final int DELETE_ID = 2;
    /**
     * The id of the direct connect to server button.
     */
    private static final int DIRECT_CONN_ID = 4;
    /**
     * The id of the edit server button.
     */
    private static final int EDITING_ID = 7;
    /**
     * The id of the refresh server list button.
     */
    private static final int REFRESH_ID = 8;

    /**
     * List of ids of default enabled buttons.
     */
    private static final List<Integer> ENABLED_BUTTONS = Arrays.asList(1, 2, 7);

    //-----Fields:--------------------------------------//

    /**
     * The list of server entries.
     */
    private ServerList serverList;
    /**
     * The list of server entry save data.
     */
    private ServerSaveData saveData;

    /**
     * Determines if the entry selection list was initialized.
     */
    private boolean listInitialized = false;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new server selection menu.
     * @param parentScreen The parent screen to exit to.
     */
    public ServerSelectMenu(final GuiScreen parentScreen) {
        super(parentScreen);
        this.saveData = new ServerSaveData();
    }

    //-----Overridden Methods:--------------------------//

    //:: GuiMultiplayer
    //:::::::::::::::::::::::::::::://

    /**
     * Handles the action of a clicked button.
     * @param button The button clicked.
     * @param menu The selection menu.
     */
    protected final void actionPerformed(
            final GuiButton button,
            final SelectMenuBase menu
    ) {
        switch (button.id) {
            case EDITING_ID:
                this.saveData.changeStatus(ServerDataStatus.EDITING);
                this.saveData.copyFrom(this.serverList.getSelectedServer());
                this.mc.displayGuiScreen(
                        new CreateEntryMenu<CreateServerMenu, ServerEntry>(
                                new CreateServerMenu(menu, this.saveData),
                                new DirectoryList<>(menu.getDirectoryList())
                        )
                );
                break;

            case JOIN_ID:
                this.connectToSelected();
                break;

            case CANCEL_ID:
                this.mc.displayGuiScreen(null);
                break;

            case DELETE_ID:
                this.saveData.changeStatus(ServerDataStatus.REMOVING);
                this.confirmClicked(true, 0);
                break;

            case REFRESH_ID:
                this.mc.displayGuiScreen(menu);
                break;

            case DIRECT_CONN_ID:
                this.saveData.changeStatus(ServerDataStatus.DIRECT_CONNECTING);
                this.saveData.copyFrom(new ServerSaveData());
                this.mc.displayGuiScreen(
                        new GuiScreenServerList(
                                this,
                                this.saveData.getServerData()
                        )
                );
                break;

            default:
                break;
        }
    }

    /**
     * Confirms the results of a click.
     * @param result True if click was successful.
     * @param id The id of the element that was clicked.
     */
    @Override
    public final void confirmClicked(final boolean result, final int id) {
        final ServerSaveLoadUtils details = this.serverList.getListDetails();
        if (result) {
            switch (saveData.getStatus()) {
                case ADDING:
                    details.addSaveData(this.saveData);
                    details.save();
                    this.serverList.setSelectedIndex(-1);
                    break;

                case EDITING:
                    this.serverList.editSelectedIndex(this.saveData);
                    details.save();
                    this.serverList.refresh();
                    break;

                case REMOVING:
                    this.serverList.delete(this.serverList.getSelectedIndex());
                    break;

                case DIRECT_CONNECTING:
                    this.connect(this.saveData.getServerData());
                    break;

                case NONE:
                    //:: Do nothing
                    break;
                default:
                    //:: Do nothing because there is nothing to do.
            }

            saveData.changeStatus(ServerDataStatus.NONE);
        } else {
            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Do nothing here because this is handled by
     * the selection menu.
     * @param mouseX N/A
     * @param mouseY N/A
     * @param mouseButton N/A
     */
    @Override
    public void mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    /**
     * Do nothing here because this is handled by
     * the selection menu.
     * @param mouseX N/A
     * @param mouseY N/A
     * @param mouseButton N/A
     */
    @Override
    public void mouseReleased(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    /**
     * Determines if a server entry can move down in the server list.
     * @param entry The entry - this parameter isn't used.
     * @param position The position of the server entry within the list.
     * @return Returns true if the the server entry can move down.
     */
    @Override
    public final boolean canMoveDown(
            final ServerListEntryNormal entry,
            final int position
    ) {
        return position < this.serverList.getPartialSize() - 1;
    }

    /**
     * Moves a server up one position in the directory list.
     * @param entry The entry that is being moved - [param not in ues]
     * @param position The position in the list of the server entry to be moved.
     * @param canMove Determines if the server entry is allowed to be moved.
     */
    @Override
    public final void moveServerUp(
            final ServerListEntryNormal entry,
            final int position,
            final boolean canMove
    ) {
        final int wholePosition = this.serverList.getWholeIndex(position);
        final int i = canMove ? 0 : wholePosition - 1;
        this.serverList.getListDetails().swapServers(wholePosition, i);
        if (this.serverList.getSelectedIndex() == position) {
            this.serverList.setSelectedIndex(i);
        }
        this.serverList.updateServerList();
    }

    /**
     * Moves a server down one position in the directory list.
     * @param entry The entry that is being moved - [param not in ues]
     * @param position The position in the list of the server entry to be moved.
     * @param canMove Determines if the server entry is allowed to be moved.
     */
    @Override
    public final void moveServerDown(
            final ServerListEntryNormal entry,
            final int position,
            final boolean canMove
    ) {
        final int wholePosition = this.serverList.getWholeIndex(position);
        final int i = (canMove)
                ? this.serverList.getPartialSize() - 1
                : wholePosition + 1;
        this.serverList.getListDetails().swapServers(wholePosition, i);
        if (serverList.getSelectedIndex() == position) {
            this.serverList.setSelectedIndex(i);
        }
        this.serverList.updateServerList();
    }

    /**
     * Connects the player to the selected server entry.
     */
    @Override
    public final void connectToSelected() {
        final int selected = this.serverList.getSelectedIndex();
        final ServerEntry entry = (selected < 0)
                ? null
                : this.serverList.getListEntry(selected);

        if (entry != null) {
            if (entry.isLan()) {
                //:: TODO: Implement lan worlds
            } else {
                this.connect(entry.getServerData());
            }
        }
    }

    //:: IMenuType
    //:::::::::::::::::::::::::::::://

    /**
     * Invokes the creation of a new server entry.
     * @param menu The selection menu.
     */
    @Override
    public final void invokeEntryCreation(final SelectMenuBase menu) {
        this.saveData.changeStatus(ServerDataStatus.ADDING);
        this.saveData.copyFrom(new ServerSaveData());
        this.mc.displayGuiScreen(
                new CreateEntryMenu<CreateServerMenu, ServerEntry>(
                        new CreateServerMenu(menu, this.saveData),
                        new DirectoryList<>(menu.getDirectoryList())
                )
        );
    }

    /**
     * Initializes the server entry list.
     * @param list The server list.
     */
    @Override
    public final void listInit(final IListType list) {
        if (!this.listInitialized) {
            this.listInitialized = true;
            this.serverList = (ServerList) list;
            this.serverList.getListDetails().loadServerList();
            this.selectRelevantServer(-1);
        }
    }

    /**
     * @return Returns the buttons that the server selection list contains.
     */
    @Override
    public final List<GuiButton> getButtonList() {
        return this.buttonList;
    }

    //-----This:----------------------------------------//

    /**
     * @return Returns the list of available servers.
     */
    public final ServerList getServers() {
        return serverList;
    }

    /**
     * Selects a server from the relevantly displayed server list.
     * @param index The indx of the server entry to select.
     */
    public final void selectRelevantServer(final int index) {
        if (listInitialized) {
            this.serverList.setSelectedIndex(index);
            this.buttonList.stream()
                    .filter(btn -> ENABLED_BUTTONS.contains(btn.id))
                    .forEach(btn -> btn.enabled = index != -1);
        }
    }

    /**
     * Connects the player to a server.
     * @param server The server the player will be connected to.
     */
    private void connect(final ServerData server) {
        net.minecraftforge.fml.client.FMLClientHandler.instance()
                .connectToServer(this, server);
    }
}
