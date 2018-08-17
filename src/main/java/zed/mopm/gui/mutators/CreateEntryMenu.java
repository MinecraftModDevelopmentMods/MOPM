package zed.mopm.gui.mutators;

import jline.internal.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.api.gui.mutators.ICreatorMenu;
import zed.mopm.gui.lists.FolderList;

import java.io.File;
import java.io.IOException;

public class CreateEntryMenu <K extends GuiScreen & ICreatorMenu, L extends IFolderPath> extends GuiScreen implements IFolderPath {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private static final int SELECTION_ID = 100;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private K invokeCreator;
    private DirectorySelectionMenu selectionMenu;

    private GuiTextField pathDisplay;
    private GuiButtonExt selectBtn;
    private String savePath;
    private File mopmSaveFile;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public CreateEntryMenu(final K invokerIn, final FolderList<L> folderList) {
        this.invokeCreator = invokerIn;
        this.selectionMenu = new DirectorySelectionMenu(this, folderList);

        this.pathDisplay = this.invokeCreator.getTextField();
        this.pathDisplay.setMaxStringLength(Integer.MAX_VALUE);
        this.mopmSaveFile = null;

        this.setPath(folderList.currentPath());
        this.setUniquePath(folderList.uniquePath());
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiScreen
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        this.invokeCreator.setWorldAndResolution(this.mc, this.width, this.height);
        this.invokeCreator.initGui();
        Keyboard.enableRepeatEvents(true);

        this.pathDisplay.x = this.width / 2 - 50;
        this.selectBtn = this.invokeCreator.getSelectionButton(this.width);
        this.addButton(selectBtn);
        for (final GuiButton btn : this.invokeCreator.getButtons()) {
            this.addButton(btn);
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.invokeCreator.drawScreen(mouseX, mouseY, partialTicks);
        this.pathDisplay.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        if (btn.id == SELECTION_ID) {
            this.mc.displayGuiScreen(this.selectionMenu);
        }
        else {
            this.invokeCreator.handleActionPerformed(btn, this);
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.invokeCreator.doKeyTyped(typedChar, keyCode);
        if (this.pathDisplay.isFocused() && keyCode == 203 || keyCode == 205 || GuiScreen.isKeyComboCtrlA(keyCode) || GuiScreen.isKeyComboCtrlC(keyCode)) {
            this.pathDisplay.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.invokeCreator.doMouseClicked(mouseX, mouseY, mouseButton);
        this.pathDisplay.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onResize(final Minecraft mcIn, final int w, final int h) {
        super.onResize(mcIn, w, h);
        this.invokeCreator.onResize(mcIn, w, h);
        this.width = w;
        this.height = h;
    }

    //:: IFolderPath
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void setPath(final String path) {
        this.pathDisplay.setText("Dir: " + path);
    }

    @Override
    public void setUniquePath(final String path) {
        this.savePath = path;
    }

    @Override
    public String getPathToDir() {
        return this.pathDisplay.getText().substring("Dir: ".length());
    }

    @Override
    @Nullable
    public File getMopmSaveFile() {
        return this.mopmSaveFile;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public String getSavePath() {
        return this.savePath;
    }

    public void toggleDisplay() {
        this.pathDisplay.setVisible(!this.pathDisplay.getVisible());
        this.selectBtn.visible = !this.selectBtn.visible;
    }

    public void setMopmSaveFile(final File file) {
        this.mopmSaveFile = file;
    }
}
