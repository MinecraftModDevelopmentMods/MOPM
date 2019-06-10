package zed.mopm.gui.menus.mutators.entries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.apache.commons.lang3.StringUtils;
import zed.mopm.api.gui.mutators.CreatorMenu;
import zed.mopm.api.gui.mutators.ICreatorMenu;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.*;
import java.util.List;

import static zed.mopm.gui.menus.mutators.entries.CreateEntryMenu.*;
import static zed.mopm.gui.utils.constants.ButtonConsts.BUTTON_HEIGHT;
import static zed.mopm.gui.utils.constants.ButtonConsts.TEXT_FIELD_WIDTH;

public class CreateWorldMenu
        extends GuiCreateWorld
        implements ICreatorMenu {

    //-----Consts:-------------------------------------//

    /**
     * The id of the dummy name field.
     */
    private static final int DUMMY_ID = 100;
    /**
     * The y position of the menu elements.
     */
    private static final int Y_POS = 163;
    /**
     * The y position of the dummy name field.
     */
    private static final int DUMMY_Y = 60;

    //-----Fields:-------------------------------------//

    /**
     * The name field that captures keyboard input when the real
     * name field is typed in. This is to keep track of the name
     * being typed in for the world.
     */
    private GuiTextField dummyNameField;
    /**
     * Determines if the name field is focused.
     */
    private boolean isToggled = false;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new world creator menu.
     * @param parentScreen The menu accessing this menu.
     */
    public CreateWorldMenu(final GuiScreen parentScreen) {
        super(parentScreen);
        this.mc = parentScreen.mc;
        this.fontRenderer = this.mc.fontRenderer;
        this.dummyNameField = new GuiTextField(
                DUMMY_ID,
                this.fontRenderer,
                (this.width / 2 - SELECT_X),
                DUMMY_Y,
                TEXT_FIELD_WIDTH,
                BUTTON_HEIGHT
        );
        this.dummyNameField.setText(I18n.format("selectWorld.newWorld"));
    }

    //-----Overridden Methods:--------------------------//

    /**
     * Initializes all of the screen elements.
     */
    @Override
    public final void initGui() {
        super.initGui();
        this.dummyNameField.setFocused(true);
    }

    /**
     * Recreates a world from the given world info.
     * @param originalWorld The world info to recreate.
     */
    @Override
    public final void recreateFromExistingWorld(final WorldInfo originalWorld) {
        super.recreateFromExistingWorld(originalWorld);
        this.dummyNameField.setText(
                I18n.format(
                        "selectWorld.newWorld.copyOf",
                        originalWorld.getWorldName()
                )
        );
    }

    //:: ICreatorMenu
    //:::::::::::::::::::::::::::::://

    /**
     * @return Returns the directory display path that will be drawn in the
     * CreateEntryMenu.
     */
    @Override
    public final GuiTextField getPathDisplay() {
        return new GuiTextField(
                1,
                Minecraft.getMinecraft().fontRenderer,
                0,
                Y_POS,
                PATH_WIDTH,
                BUTTON_HEIGHT
        );
    }

    /**
     * @param screenWidth The width of the screen.
     * @return Returns the button that will be drawn in the
     * CreateEntryMenu. This button opens the SelectDirectoryMenu.
     */
    @Override
    public final GuiButtonExt getPathSelectButton(final int screenWidth) {
        return new GuiButtonExt(
                SELECTION_ID,
                (screenWidth / 2 - SELECT_X),
                Y_POS,
                SELECT_WIDTH,
                BUTTON_HEIGHT,
                "Select"
        );
    }

    /**
     * @return Returns the list of buttons that this menu owns.
     */
    @Override
    public final List<GuiButton> getButtons() {
        return this.buttonList;
    }

    /**
     * Replaces {@link #actionPerformed(GuiButton)}.
     * Handles the action of the button pressed.
     * @param btn The button clicked on.
     * @param entryMenu The create entry menu that invoked this menu.
     */
    @Override
    public final void handleActionPerformed(
            final GuiButton btn,
            final CreateEntryMenu entryMenu
    ) {
        switch (btn.id) {
            case CreatorMenu.CREATION_ID:
                entryMenu.setMopmSaveFile(
                        writeSaveData(entryMenu.getSavePath())
                );
                break;

            case CreatorMenu.TOGGLE_DISPLAY_ID:
                entryMenu.toggleDisplay();
                this.isToggled = !this.isToggled;
                break;

            default:
                //:: Should not be reached. If reached, button has invalid id.
                break;
        }
    }

    /**
     * Replaces {@link #keyTyped(char, int)}.
     * Handles the keyboard input.
     *
     * @param typedChar The character of the key typed.
     * @param keyCode   The code of the key typed.
     * @throws IOException Thrown when given invalid keyboard input.
     */
    @Override
    public final void doKeyTyped(
            final char typedChar,
            final int keyCode
    ) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.dummyNameField.isFocused()) {
            this.dummyNameField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    /**
     * Replaces {@link #mouseClicked(int, int, int)}.
     * Handles the click of the mouse.
     *
     * @param mouseX      The x location of the mouse click.
     * @param mouseY      The y location of the mouse click.
     * @param mouseButton The mouse button clicked.
     * @throws IOException Thrown when given invalid mouse clicks.
     */
    @Override
    public final void doMouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.isToggled) {
            this.dummyNameField.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Replaces {@link #mouseReleased(int, int, int)}.
     * Handles the release of the mouse.
     *
     * @param mouseX The x location of the mouse release.
     * @param mouseY The y location of the mouse release.
     * @param state  The state of the mouse.
     */
    @Override
    public final void doMouseReleased(
            final int mouseX,
            final int mouseY,
            final int state
    ) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    //-----This:----------------------------------------//

    /**
     * Returns the save directory path of the world folder.
     * This will be the folder in which the mopm_save.dat is stored
     * in.
     *
     * @return Returns the path on disc to save the world entry to.
     */
    private String getSaveDirectory() {
        String saveDirName = this.dummyNameField.getText().trim();

        for (char c0 : ChatAllowedCharacters.ILLEGAL_FILE_CHARACTERS) {
            saveDirName = saveDirName.replace(c0, '_');
        }
        if (StringUtils.isEmpty(saveDirName)) {
            saveDirName = "World";
        }

        return getUncollidingSaveDirName(this.mc.getSaveLoader(), saveDirName);
    }

    /**
     * Writes the directory path location of the world to the world save data.
     *
     * @param savePath The virtual directory path where the world is
     *                 contained in.
     * @return A file, either the save file or the base game directory.
     */
    private File writeSaveData(final String savePath) {
        try {
            final File mopmSaveFile = this.mc.getSaveLoader()
                    .getFile(
                            this.getSaveDirectory(),
                            MOPMLiterals.MOPM_SAVE_DAT
                    );
            mopmSaveFile.getParentFile().mkdirs();

            References.LOG.info(
                    "Writing save path: "
                            + savePath
                            + " : save to : "
                            + mopmSaveFile.getAbsolutePath()
            );
            try (
                    DataOutputStream write =
                            new DataOutputStream(
                                    new FileOutputStream(mopmSaveFile)
                            )
            ) {
                write.write(savePath.getBytes());
            }
            return mopmSaveFile;
        } catch (IOException e) {
            References.LOG.info("Error occurred on world creation: ", e);
        }
        return new File(Minecraft.getMinecraft().gameDir, "");
    }
}
