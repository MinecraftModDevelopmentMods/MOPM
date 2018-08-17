package zed.mopm.gui.mutators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.api.gui.mutators.ICreatorMenu;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

public class CreateWorldEntryMenu extends GuiCreateWorld implements ICreatorMenu {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://


    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public CreateWorldEntryMenu(final GuiScreen parentScreen) {
        super(parentScreen);
        this.mc = parentScreen.mc;
        this.fontRenderer = this.mc.fontRenderer;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: ICreatorMenu
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public GuiTextField getTextField() {
        return new GuiTextField(1, Minecraft.getMinecraft().fontRenderer, 0, 163, 150, 20);
    }

    @Override
    public GuiButtonExt getSelectionButton(final int screenWidth) {
        return new GuiButtonExt(100, screenWidth / 2 - 100, 163, 45, 20, "Select");
    }

    @Override
    public List<GuiButton> getButtons() {
        return this.buttonList;
    }

    @Override
    public void handleActionPerformed(final GuiButton btn, final CreateEntryMenu entryMenu) throws IOException {
        switch (btn.id) {
            case CREATION_ID:
                writeSaveData(entryMenu.getMopmSaveFile(), entryMenu.getSavePath());
                break;

            case TOGGLE_DISPLAY_ID:
                entryMenu.toggleDisplay();
                break;

            default:
                /*
                 * This should not be reached
                 */
                break;
        }

        super.actionPerformed(btn);

    }

    @Override
    public void doKeyTyped(final char typedChar, final int keyCode) throws IOException {
        //this.keyTyped(typedChar, keyCode);
    }

    @Override
    public void doMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        //this.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void doMouseReleased(int mouseX, int mouseY, int state) {
        //this.mouseReleased(mouseX, mouseY, state);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private void writeSaveData(File mopmSaveFile, final String savePath) {
        String worldDirName;
        try {
            Field field = GuiCreateWorld.class.getDeclaredField("saveDirName");
            field.setAccessible(true);
            worldDirName = (String) field.get(this);

            mopmSaveFile = Minecraft.getMinecraft().getSaveLoader().getFile(worldDirName, MOPMLiterals.MOPM_SAVE_DAT);
            mopmSaveFile.getParentFile().mkdirs();
            try (DataOutputStream write = new DataOutputStream(new FileOutputStream(mopmSaveFile))) {
                write.write(savePath.getBytes());
            }
        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            References.LOG.info("", e);
        }
    }
}
