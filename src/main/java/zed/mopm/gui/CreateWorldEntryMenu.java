package zed.mopm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.data.Directory;
import zed.mopm.gui.buttons.FolderButton;
import zed.mopm.gui.lists.FolderList;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Scanner;

public class CreateWorldEntryMenu extends GuiCreateWorld implements IFolderPath {
    private GuiScreen parentIn;
    private DirectorySelectionMenu selectDir;

    private GuiButtonExt folderSelection;
    private GuiTextField pathDisplay;
    private String savePath;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public CreateWorldEntryMenu(GuiScreen parentScreen, FolderList folderList) {
        super(parentScreen);
        parentIn = parentScreen;
        selectDir = new DirectorySelectionMenu(this, folderList);

        pathDisplay = new GuiTextField(1, Minecraft.getMinecraft().fontRenderer, 0, 163, 150, 20);
        pathDisplay.setMaxStringLength(Integer.MAX_VALUE);
        this.setPath(folderList.currentPath());
        this.setUniquePath(folderList.uniquePath());
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        pathDisplay.x = this.width / 2 - 50;
        folderSelection = new GuiButtonExt(100, this.width / 2 - 100, 163, 45, 20, "Select");
        this.buttonList.add(folderSelection);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.pathDisplay.drawTextBox();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        System.out.println("BUTTON ID: " + button.id);
        switch (button.id) {

            case 0: {
                String worldDirName;
                try {
                    Field field = GuiCreateWorld.class.getDeclaredField("saveDirName");
                    field.setAccessible(true);
                    worldDirName = (String)field.get(this);
                    System.out.println("MC DIR PATH: " + Minecraft.getMinecraft().getSaveLoader().getFile(worldDirName, "text.txt"));
                    File createSavePath = Minecraft.getMinecraft().getSaveLoader().getFile(worldDirName, "mopm_save.dat");
                    createSavePath.getParentFile().mkdirs();
                    createSavePath.setWritable(true);
                    DataOutputStream write = new DataOutputStream(new FileOutputStream(createSavePath));
                    write.write(this.savePath.getBytes());
                    write.close();
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            break;

            // 100: Select the directory the world should be stored in.
            case 100: {
                this.mc.displayGuiScreen(this.selectDir);
            }
            break;

            case 3: {
                this.pathDisplay.setVisible(!this.pathDisplay.getVisible());
                this.folderSelection.visible = !this.folderSelection.visible;
            }
            break;
        }

        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.pathDisplay.isFocused() && keyCode == 203 || keyCode == 205 || GuiScreen.isKeyComboCtrlA(keyCode) || GuiScreen.isKeyComboCtrlC(keyCode)) {
            this.pathDisplay.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.pathDisplay.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void setPath(String path) {
        pathDisplay.setText("Dir: " + path);
    }

    @Override
    public void setUniquePath(String path) {
        savePath = new String(path);
    }

    @Override
    public String getPathToDir() {
        return pathDisplay.getText().substring("Dir: ".length());
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
}
