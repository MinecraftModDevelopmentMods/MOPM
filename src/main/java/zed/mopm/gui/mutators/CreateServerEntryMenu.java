package zed.mopm.gui.mutators;

import jline.internal.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.api.data.ServerDataStatus;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.ServerSaveData;
import zed.mopm.gui.lists.FolderList;
import java.io.File;
import java.io.IOException;

public class CreateServerEntryMenu extends GuiScreenAddServer implements IFolderPath {
    private GuiScreen parentIn;
    private DirectorySelectionMenu selectDir;

    private ServerSaveData saveData;
    private GuiButtonExt folderSelection;
    private GuiTextField pathDisplay;
    private String savePath;
    private File mopmSaveFile;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public CreateServerEntryMenu(final GuiScreen parentScreenIn, final ServerSaveData serverDataIn, final FolderList<ServerEntry> folderList) {
        super(parentScreenIn, serverDataIn.getServerData());
        this.parentIn = parentScreenIn;
        this.selectDir = new DirectorySelectionMenu(this, folderList);
        this.saveData = serverDataIn;

        this.pathDisplay = new GuiTextField(1, Minecraft.getMinecraft().fontRenderer, 0, 30, 150, 20);
        this.pathDisplay.setMaxStringLength(Integer.MAX_VALUE);
        this.mopmSaveFile = null;
        this.setPath(folderList.currentPath());
        this.setUniquePath(folderList.uniquePath());
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     */
    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        pathDisplay.x = this.width / 2 - 50;
        folderSelection = new GuiButtonExt(100, this.width / 2 - 100, 30, 45, 20, "Select");
        this.addButton(folderSelection);
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.pathDisplay.drawTextBox();
    }

    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        final ServerDataStatus status = this.saveData.getStatus();
        this.saveData.changeStatus(ServerDataStatus.NONE);
        super.actionPerformed(button);
        this.saveData.changeStatus(status);

        switch (button.id) {

            //:: Create server entry and save it to the servers.dat
            case 0: {
                this.saveData.setSavePath(savePath);
                this.parentIn.confirmClicked(true, 0);
            }
            break;

            // 100: Select the directory the world should be stored in.
            case 100: {
                this.mc.displayGuiScreen(this.selectDir);
            }
            break;

            default: {
                this.mc.displayGuiScreen(this.parentIn);
            }
            break;
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.pathDisplay.isFocused() && keyCode == 203 || keyCode == 205 || GuiScreen.isKeyComboCtrlA(keyCode) || GuiScreen.isKeyComboCtrlC(keyCode)) {
            this.pathDisplay.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.pathDisplay.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onResize(final Minecraft mcIn, final int w, final int h) {
        super.onResize(mcIn, w, h);
        this.width = w;
        this.height = h;
    }

    @Override
    public void setPath(final String path) {
        pathDisplay.setText("Dir: " + path);
    }

    @Override
    public void setUniquePath(final String path) {
        savePath = path;
    }

    @Override
    public String getPathToDir() {
        return pathDisplay.getText().substring("Dir: ".length());
    }

    @Override
    @Nullable
    public File getMopmSaveFile() {
        return mopmSaveFile;
    }
}
