package zed.mopm.gui.mutators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.api.data.ServerDataStatus;
import zed.mopm.api.gui.mutators.CreatorMenu;
import zed.mopm.api.gui.mutators.ICreatorMenu;
import zed.mopm.data.ServerSaveData;
import java.io.IOException;
import java.net.IDN;
import java.util.List;

public class CreateServerEntryMenu extends GuiScreenAddServer implements ICreatorMenu {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private static final int RESOURCE_BTN_ID = 2;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private GuiScreen parentIn;

    private ServerSaveData saveData;
    private GuiTextField ipField;
    private GuiTextField nameField;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public CreateServerEntryMenu(final GuiScreen parentScreenIn, final ServerSaveData serverDataIn) {
        super(parentScreenIn, serverDataIn.getServerData());
        this.parentIn = parentScreenIn;

        this.saveData = serverDataIn;
        this.mc = parentScreenIn.mc;
        this.fontRenderer = this.mc.fontRenderer;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: ICreatorMenu
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public GuiTextField getTextField() {
        return new GuiTextField(1, Minecraft.getMinecraft().fontRenderer, 0, 30, 150, 20);
    }

    @Override
    public GuiButtonExt getSelectionButton(final int screenWidth) {
        return new GuiButtonExt(100, screenWidth / 2 - 100, 30, 45, 20, "Select");
    }

    @Override
    public List<GuiButton> getButtons() {
        return this.buttonList;
    }

    @Override
    public void handleActionPerformed(final GuiButton btn, final CreateEntryMenu entryMenu) throws IOException {
        final ServerDataStatus status = this.saveData.getStatus();
        this.saveData.changeStatus(ServerDataStatus.NONE);
        super.actionPerformed(btn);
        this.saveData.changeStatus(status);

        switch (btn.id) {
            case CreatorMenu.CREATION_ID:
                this.saveData.getServerData().serverName = this.nameField.getText();
                this.saveData.getServerData().serverIP = this.ipField.getText();
                this.saveData.setSavePath(entryMenu.getSavePath());
                this.parentIn.confirmClicked(true, 0);
                break;

            case RESOURCE_BTN_ID:
                //:: Do nothing! We dont want to execute the default case. ID 2 is handled by the super actionPerformed call.
                break;

            default:
                this.mc.displayGuiScreen(this.parentIn);
                break;
        }
    }

    @Override
    public void doKeyTyped(final char typedChar, final int keyCode) throws IOException {
        this.keyTyped(typedChar, keyCode);
    }

    @Override
    public void doMouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void doMouseReleased(int mouseX, int mouseY, int state) {
        this.mouseReleased(mouseX, mouseY, state);
    }

    //:: GuiScreenAddServer
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        super.initGui();
        this.nameField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 66, 200, 20);
        this.nameField.setFocused(true);
        this.nameField.setText(this.saveData.getServerData().serverName);

        this.ipField = new GuiTextField(1, this.fontRenderer, this.width / 2 - 100, 106, 200, 20);
        this.ipField.setMaxStringLength(128);
        this.ipField.setText(this.saveData.getServerData().serverIP);
        this.ipField.setValidator(validator -> {
            /**
             * This validator code has been taken from the validator predicate in the GuiScreenAddServer class.
             */
            if (StringUtils.isNullOrEmpty(validator)) {
                return true;
            }
            else
            {
                String[] astring = validator.split(":");

                if (astring.length == 0) {
                    return true;
                }
                else {
                    try {
                        IDN.toASCII(astring[0]);
                        return true;
                    }
                    catch (IllegalArgumentException var4) {
                        return false;
                    }
                }
            }
        });
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.nameField.drawTextBox();
        this.ipField.drawTextBox();
    }

    @Override
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        this.nameField.textboxKeyTyped(typedChar, keyCode);
        this.ipField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 15) {
            this.nameField.setFocused(!this.nameField.isFocused());
        }
        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed(this.buttonList.get(0));
        }

        (this.buttonList.get(0)).enabled = !this.ipField.getText().isEmpty() && this.ipField.getText().split(":").length > 0 && !this.nameField.getText().isEmpty();
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.ipField.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
