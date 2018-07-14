package zed.mopm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import zed.mopm.api.data.IFolderMenu;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.WorldList;
import zed.mopm.gui.mutators.CreateFolderEntryMenu;
import zed.mopm.gui.mutators.CreateWorldEntryMenu;
import zed.mopm.util.GuiUtils;
import zed.mopm.util.References;

import java.io.IOException;

import static zed.mopm.gui.SinglePlayerMenu.SelectedList.BUTTONS;
import static zed.mopm.gui.SinglePlayerMenu.SelectedList.FOLDER_LIST;
import static zed.mopm.gui.SinglePlayerMenu.SelectedList.WORLD_LIST;

public class SinglePlayerMenu extends GuiWorldSelection implements IFolderMenu {

    enum SelectedList {
        WORLD_LIST,
        FOLDER_LIST,
        BUTTONS
    }

    private FolderList<WorldEntry> directories;
    private WorldList worldSelectionList;
    private SelectedList listFocus;

    private GuiButtonExt createFolderEntryButton;
    private GuiButtonExt back;
    private GuiButtonExt print;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public SinglePlayerMenu(GuiScreen screenIn) {
        super(screenIn);
        listFocus = WORLD_LIST;

        createFolderEntryButton = new GuiButtonExt(99, 30, 10, 15, 15, "+");
        back = new GuiButtonExt(101, 10, 10, 20, 15, "<<");
        print = new GuiButtonExt(102, 45, 10, 15, 15, "/");
        directories = new FolderList<>(this, 100,0, 32, 20, Minecraft.getMinecraft().gameDir);
        worldSelectionList = new WorldList(this, Minecraft.getMinecraft(), 36);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        this.addButton(createFolderEntryButton);
        this.addButton(back);
        this.addButton(print);
        this.directories.setHeight(this.height);

        this.directories.save();
        this.worldSelectionList.refreshList();
        this.worldSelectionList.setDimensions(this.width + directories.width, this.height - 100, 32, this.height - 64);
        this.refreshDirectoryEntryList();

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        worldSelectionList.drawScreen(mouseX, mouseY, partialTicks);
        directories.drawScreen(mouseX, mouseY, partialTicks);
        GuiUtils.drawTexturedRect(0.0D, (double)(this.height - 64), (double)(this.width), (double) this.height, (double)this.zLevel + 1, 64, 64, 64, 255, 0, OPTIONS_BACKGROUND, this.mc);

        for (GuiButton button : this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            switch (button.id) {
                case 3: {
                    this.mc.displayGuiScreen(new CreateWorldEntryMenu(this, new FolderList(this.directories)));
                }
                break;

                case 99: {
                    CreateFolderEntryMenu mkDir = new CreateFolderEntryMenu(this);
                    Minecraft.getMinecraft().displayGuiScreen(mkDir);
                }
                break;

                case 101: {
                    directories.back();
                }
                break;

                case 102: {
                    this.directories.save();
                }
                break;

                default: {
                    super.actionPerformed(button);
                }
            }
        }
        catch (IOException e) {
            References.LOG.error("", e);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        switch (this.listFocus) {
            case FOLDER_LIST:
                this.directories.handleMouseInput();
                break;
            case WORLD_LIST:
                this.worldSelectionList.handleMouseInput();
                break;
            case BUTTONS:
                //Nothing to handle here
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseY >= 32 && mouseY <= this.height - 64) {
            if (mouseX >= 0 && mouseX <= this.directories.width) {
                this.listFocus = FOLDER_LIST;
                this.directories.mouseClicked(mouseX, mouseY, mouseButton);
            }
            else {
                this.listFocus = WORLD_LIST;
                this.worldSelectionList.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        else {
            this.listFocus = BUTTONS;
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        switch (this.listFocus) {
            case FOLDER_LIST:
                this.directories.mouseReleased(mouseX, mouseY, state);
                break;
            case WORLD_LIST:
                this.worldSelectionList.mouseReleased(mouseX, mouseY, state);
                break;
            case BUTTONS:
                super.mouseReleased(mouseX, mouseY, state);
                break;
        }
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        directories.setHeight(h);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public FolderList<WorldEntry> getDirectoryList() {
        return this.directories;
    }

    @Override
    public void refreshDirectoryEntryList() {
        this.worldSelectionList.displayWorlds(this.directories.getFolder().getEntries());
    }
}
