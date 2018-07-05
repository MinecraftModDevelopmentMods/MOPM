package zed.mopm.gui.mutators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.util.References;

import java.io.*;
import java.lang.reflect.Field;

public class CreateWorldEntryMenu extends GuiCreateWorld implements IFolderPath {
    private DirectorySelectionMenu selectDir;

    private GuiButtonExt folderSelection;
    private GuiTextField pathDisplay;
    private String savePath;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public CreateWorldEntryMenu(GuiScreen parentScreen, FolderList folderList) {
        super(parentScreen);
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
        switch (button.id) {

            case 0: {
                String worldDirName;
                try {
                    Field field = GuiCreateWorld.class.getDeclaredField("saveDirName");
                    field.setAccessible(true);
                    worldDirName = (String)field.get(this);

                    File createSavePath = Minecraft.getMinecraft().getSaveLoader().getFile(worldDirName, "mopm_save.dat");
                    try (DataOutputStream write = new DataOutputStream(new FileOutputStream(createSavePath))) {
                        createSavePath.getParentFile().mkdirs();
                        write.write(this.savePath.getBytes());
                    }
                } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
                    References.LOG.info("", e);
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

            default: {
                /* This should never be reached!
                 * if this is reached, there was a button with a wrong id.
                 */
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
        savePath = path;
    }

    @Override
    public String getPathToDir() {
        return pathDisplay.getText().substring("Dir: ".length());
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
}
