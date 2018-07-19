package zed.mopm.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import zed.mopm.api.gui.IMenuType;
import zed.mopm.data.ServerEntry;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.ServerList;
import zed.mopm.gui.mutators.CreateServerEntryMenu;

import java.util.List;

public class MultiplayerMenu extends GuiMultiplayer implements IMenuType {

    private ServerData saveData;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public MultiplayerMenu(GuiScreen parentScreen) {
        super(parentScreen);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    protected void actionPerformed(GuiButton button, FolderList<ServerEntry> directories, ServerList selectionList) {
        switch (button.id) {
            default:
                break;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    @Override
    public void invokeEntryCreation(ModifiableMenu menu) {
        this.saveData.copyFrom(new ServerData(I18n.format("selectServer.defaultName"), "", false));
        this.mc.displayGuiScreen(new CreateServerEntryMenu(this, this.saveData, new FolderList(menu.getDirectoryList())));
    }

    @Override
    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }
}
