package zed.mopm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.*;
import zed.mopm.api.gui.*;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.buttons.ToolTipButton;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.ServerList;
import zed.mopm.gui.lists.WorldList;
import zed.mopm.gui.mutators.CreateFolderEntryMenu;
import zed.mopm.util.GuiUtils;
import zed.mopm.util.References;

import java.io.IOException;

public class ModifiableMenu<K extends GuiScreen & IMenuType, J extends GuiListExtended.IGuiListEntry & IFolderPath, L extends GuiListExtended & IModifiableList & IListType<J>> extends GuiScreen implements IFolderMenu {
    private K invokeScreen;

    private FolderList<J> directories;
    private L entrySelectionList;
    private SelectedList listFocus;

    private GuiButtonExt createFolderEntryButton;
    private GuiButtonExt back;
    private GuiButtonExt print;
    private GuiButtonExt hidePath;

    private GuiTextField directoryDisplay;

    public ModifiableMenu(final K wrappedScreenIn, final Minecraft clientIn) {
        this.invokeScreen = wrappedScreenIn;
        this.listFocus = SelectedList.ENTRY_LIST;

        this.createFolderEntryButton = new ToolTipButton(99, 30, 10, 15, 15, "+", "New Folder");
        this.back = new ToolTipButton(101, 10, 10, 20, 15, "<<", "Back");
        this.print = new ToolTipButton(102, 45, 10, 15, 15, "/", "Save");
        this.hidePath = new ToolTipButton(103, 65, 2, 5, 5, "", "Hide");

        this.directories = new FolderList<>(this, 100, 0, 32, 20, clientIn.gameDir);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        invokeScreen.setWorldAndResolution(this.mc, this.width, this.height);
        invokeScreen.initGui();

        this.directoryDisplay = new GuiTextField(1, this.fontRenderer, 65, 10, (this.directoryDisplay == null) ? this.width - 70 : this.directoryDisplay.width, 15);
        this.directoryDisplay.setMaxStringLength(Integer.MAX_VALUE);
        this.directoryDisplay.setText(this.directories.currentPath());

        this.buttonList.clear();
        this.addButton(createFolderEntryButton);
        this.addButton(back);
        this.addButton(print);
        this.addButton(hidePath);

        for (GuiButton button : invokeScreen.getButtonList()) {
            this.addButton(button);
        }

        this.directories.setHeight(this.height);

        this.directories.save();
        if (entrySelectionList instanceof WorldList) {
            ((WorldList) this.entrySelectionList).refreshList();
        }
        this.entrySelectionList.setDimensions(this.width + directories.width, this.height - 100, 32, this.height - 64);
        this.refreshDirectoryEntryList();

        super.initGui();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        entrySelectionList.drawScreen(mouseX, mouseY, partialTicks);
        directories.drawScreen(mouseX, mouseY, partialTicks);
        GuiUtils.drawTexturedRect(0.0D, (double) (this.height - 64), (double) (this.width), (double) this.height, (double) this.zLevel + 1, 64, 64, 64, 255, 0, OPTIONS_BACKGROUND, this.mc);
        this.directoryDisplay.drawTextBox();

        int mouseOver = -1;
        for (GuiButton button : this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY, partialTicks);
            if (button.isMouseOver()) {
                mouseOver = this.buttonList.indexOf(button);
            }
        }

        if (mouseOver != -1 && this.buttonList.get(mouseOver) instanceof ToolTipButton) {
            ((ToolTipButton) this.buttonList.get(mouseOver)).drawHoverState(this.mc, mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(final GuiButton button) {

        switch (button.id) {
            //:: Create new entry
            case 3:
                this.invokeScreen.invokeEntryCreation(this);
                break;

            //:: Create new subdirectory
            case 99:
                this.mc.displayGuiScreen(new CreateFolderEntryMenu(this));
                break;

            //:: Jump go back down in the directory path
            case 101:
                directories.back();
                this.directoryDisplay.setText(this.directories.currentPath());
                break;

            //:: Save directory tree to a file
            case 102:
                References.LOG.info("Directory Tree:");
                this.directories.print();
                this.directories.save();
                break;

            //:: Hide or Unhide directory display
            case 103:
                this.toggleDirectoryDisplay();
                break;

            default:
                if (this.invokeScreen instanceof SinglePlayerMenu) {
                    ((SinglePlayerMenu) this.invokeScreen).actionPerformed(button, (FolderList<WorldEntry>) this.directories, (WorldList) this.entrySelectionList);
                } else if (this.entrySelectionList instanceof ServerList) {
                    ((MultiplayerMenu) this.invokeScreen).actionPerformed(button, (FolderList<ServerEntry>) this.directories, (ServerList) this.entrySelectionList);
                }
                break;
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        References.LOG.info("KEY CODE: " + keyCode);
        if (this.directoryDisplay.isFocused() && keyCode == 203 || keyCode == 205 || GuiScreen.isKeyComboCtrlA(keyCode) || GuiScreen.isKeyComboCtrlC(keyCode)) {
            this.directoryDisplay.textboxKeyTyped(typedChar, keyCode);
        } else if (keyCode == 1) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        invokeScreen.handleMouseInput();
        switch (this.listFocus) {

            //:: The directory list is in focus
            // - Handle the mouse input of the directory list and only the directory list
            case FOLDER_LIST:
                this.directories.handleMouseInput();
                break;

            //:: The world list is in focus
            // - Handle the mouse input of the world list and only the world list
            case ENTRY_LIST:
                this.entrySelectionList.handleMouseInput();
                break;

            //:: Neither the directory list or the world list is in focus
            // - Do nothing because there is nothing to handle
            default:
                //:: This should not be reached
                break;
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseY >= 32 && mouseY <= this.height - 64) {
            if (mouseX >= 0 && mouseX <= this.directories.width) {
                this.listFocus = SelectedList.FOLDER_LIST;
                this.directories.mouseClicked(mouseX, mouseY, mouseButton);
            } else {
                this.listFocus = SelectedList.ENTRY_LIST;
                this.entrySelectionList.mouseClicked(mouseX, mouseY, mouseButton);
            }
        } else {
            this.listFocus = SelectedList.BUTTONS;
            this.directoryDisplay.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        super.mouseReleased(mouseX, mouseY, state);
        switch (this.listFocus) {

            //:: The folder list has been put into focus
            case FOLDER_LIST:
                this.directories.mouseReleased(mouseX, mouseY, state);
                this.directoryDisplay.setText(this.directories.currentPath());
                break;

            //:: The world list has been put into focus
            case ENTRY_LIST:
                this.entrySelectionList.mouseReleased(mouseX, mouseY, state);
                break;

            //:: The world list and folder list is not in focus
            case BUTTONS:
                //:: There is nothing to handle when buttons are in focus
                break;

            default:
                //:: This should not be reached
                break;

        }
    }

    @Override
    public void onResize(final Minecraft mcIn, final int w, final int h) {
        super.onResize(mcIn, w, h);
        directories.setHeight(h);
        this.directoryDisplay.width = (this.directoryDisplay.width > 5) ? this.width - 70 : 5;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public FolderList<J> getDirectoryList() {
        return this.directories;
    }

    @Override
    public void refreshDirectoryEntryList() {
        this.entrySelectionList.display(this.directories.getFolder().getEntries());
    }

    private void toggleDirectoryDisplay() {
        if (this.directoryDisplay.width > 5) {
            ((ToolTipButton) this.hidePath).setToolTip("Unhide");
            this.directoryDisplay.width = 5;
            this.directoryDisplay.setText("");
        } else {
            ((ToolTipButton) this.hidePath).setToolTip("Hide");
            this.directoryDisplay.width = this.width - 70;
            this.directoryDisplay.setText(this.directories.currentPath());
        }
    }

    public K getInvokeScreen() {
        return this.invokeScreen;
    }

    public void setContainingList(final L containingList) {
        this.entrySelectionList = containingList;
    }
}
