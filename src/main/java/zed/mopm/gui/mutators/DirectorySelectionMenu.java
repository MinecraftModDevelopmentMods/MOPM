package zed.mopm.gui.mutators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.util.ColorUtils;

import java.io.IOException;

public class DirectorySelectionMenu extends GuiScreen {
    private GuiScreen parentIn;
    private FolderList folderListIn;

    private GuiButtonExt exit;
    private GuiButtonExt back;
    private GuiButtonExt confirm;
    private GuiTextField pathDisplay;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public DirectorySelectionMenu(GuiScreen parentIn, FolderList folderList) {
        this.parentIn = parentIn;
        folderListIn = folderList;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.exit = new GuiButtonExt(1, this.width - 30, 10, 15, 15, "X");
        this.back = new GuiButtonExt(2, 10, 10, 20, 15, "<<");
        this.confirm = new GuiButtonExt(3, this.width - 55, this.height - 63 + 20, 50, 20, "Select");
        this.pathDisplay = new GuiTextField(1, this.fontRenderer, 10, this.height - 63 + 20, this.width - 70, 20);

        this.addButton(back);
        this.addButton(exit);
        this.addButton(confirm);

        this.pathDisplay.setMaxStringLength(Integer.MAX_VALUE);
        this.pathDisplay.setText(this.folderListIn.currentPath());

        this.folderListIn.width = this.width;
        this.folderListIn.height = this.height;
        this.folderListIn.top = 32;
        this.folderListIn.bottom = this.height - 64;
        this.folderListIn.left = (this.width / 2) - 100;
        this.folderListIn.right = (this.width / 2) + 100;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen(this.parentIn);
            }
            break;

            case 2: {
                folderListIn.back();
            }
            break;

            case 3: {
                ((IFolderPath)parentIn).setPath(this.folderListIn.currentPath());
                ((IFolderPath)parentIn).setUniquePath(this.folderListIn.uniquePath());
                this.mc.displayGuiScreen(this.parentIn);
            }
            break;

            default: {
                // This should never be reached
            }
            break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (this.pathDisplay.isFocused() && keyCode == 203 || keyCode == 205 || GuiScreen.isKeyComboCtrlA(keyCode) || GuiScreen.isKeyComboCtrlC(keyCode)) {
            this.pathDisplay.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.folderListIn.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.pathDisplay.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.pathDisplay.isFocused()) {
            this.folderListIn.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.folderListIn.mouseReleased(mouseX, mouseY, state);
        this.pathDisplay.setText(this.folderListIn.currentPath());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        this.folderListIn.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();
        this.pathDisplay.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawDefaultBackground() {
        //:: Bounding box
        int thickness = 20;
        int black = ColorUtils.getARGB(0, 0, 0, 255);
        int transparent = ColorUtils.getARGB(0, 0, 0, 0);

        this.drawGradientRect(0, 0, this.width, thickness, black, transparent);                                                         // Top Horizontal
        this.drawGradientRect(this.width, this.height, 0, this.height - thickness, black, transparent);                             // Bottom Horizontal

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {

        this.parentIn.onResize(mcIn, w, h);
        this.width = w;
        this.height = h;

        this.folderListIn.width = this.width;
        this.folderListIn.bottom = this.height - 64;
        this.folderListIn.left = (this.width / 2) - 100;
        this.folderListIn.right = (this.width / 2) + 100;

        this.pathDisplay.width = this.width - 70;
        this.confirm.x = this.width - 55;

        this.exit.x = this.width - 30;
        this.confirm.y = this.height - 63 + 20;
        this.pathDisplay.y = this.height - 63 + 20;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.pathDisplay.updateCursorCounter();
    }
}