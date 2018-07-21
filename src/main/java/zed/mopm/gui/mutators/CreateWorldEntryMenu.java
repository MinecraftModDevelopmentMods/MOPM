package zed.mopm.gui.mutators;

import jline.internal.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.*;
import java.lang.reflect.Field;

public class CreateWorldEntryMenu extends GuiCreateWorld implements IFolderPath {
    private DirectorySelectionMenu selectDir;

    private GuiButtonExt folderSelection;
    private GuiTextField pathDisplay;
    private String savePath;
    private File mopmSaveData;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public CreateWorldEntryMenu(final GuiScreen parentScreen, final FolderList folderList) {
        super(parentScreen);
        selectDir = new DirectorySelectionMenu(this, folderList);

        pathDisplay = new GuiTextField(1, Minecraft.getMinecraft().fontRenderer, 0, 163, 150, 20);
        pathDisplay.setMaxStringLength(Integer.MAX_VALUE);
        mopmSaveData = null;
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
        folderSelection = new GuiButtonExt(100, this.width / 2 - 100, 163, 45, 20, "Select");
        this.addButton(folderSelection);
    }

    /**
     *
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.pathDisplay.drawTextBox();
    }

    /**
     *
     * @param button
     * @throws IOException
     */
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {

            case 0: {
                String worldDirName;
                try {
                    Field field = GuiCreateWorld.class.getDeclaredField("saveDirName");
                    field.setAccessible(true);
                    worldDirName = (String) field.get(this);

                    mopmSaveData = Minecraft.getMinecraft().getSaveLoader().getFile(worldDirName, MOPMLiterals.MOPM_SAVE);
                    mopmSaveData.getParentFile().mkdirs();
                    try (DataOutputStream write = new DataOutputStream(new FileOutputStream(mopmSaveData))) {
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

    /**
     *
     * @param typedChar
     * @param keyCode
     * @throws IOException
     */
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.pathDisplay.isFocused() && keyCode == 203 || keyCode == 205 || GuiScreen.isKeyComboCtrlA(keyCode) || GuiScreen.isKeyComboCtrlC(keyCode)) {
            this.pathDisplay.textboxKeyTyped(typedChar, keyCode);
        }
    }

    /**
     *
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     * @throws IOException
     */
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.pathDisplay.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     *
     * @param path
     */
    @Override
    public void setPath(final String path) {
        pathDisplay.setText("Dir: " + path);
    }

    /**
     *
     * @param path
     */
    @Override
    public void setUniquePath(final String path) {
        savePath = path;
    }

    /**
     *
     * @return
     */
    @Override
    public String getPathToDir() {
        return pathDisplay.getText().substring("Dir: ".length());
    }

    /**
     *
     * @return
     */
    @Override
    @Nullable
    public File getMopmSaveData() {
        return mopmSaveData;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
}
