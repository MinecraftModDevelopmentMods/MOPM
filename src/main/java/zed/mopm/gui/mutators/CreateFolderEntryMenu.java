package zed.mopm.gui.mutators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.gui.IFolderMenu;
import zed.mopm.util.ColorUtils;
import zed.mopm.util.GuiUtils;

import java.io.IOException;

public class CreateFolderEntryMenu extends GuiScreen {
    private GuiScreen parentIn;
    private boolean doDrawScreen = true;

    private GuiTextField folderNameInquiry;
    private GuiButtonExt exit;
    private GuiButtonExt createFolder;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public CreateFolderEntryMenu(GuiScreen parentIn) {
        this.parentIn = parentIn;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.exit = new GuiButtonExt(1, this.width - 30, 10, 15, 15, "X");
        this.createFolder = new GuiButtonExt(2, 50 + 200 + 5, this.height - 63 + 20, 55, 20, "Create");
        this.addButton(exit);
        this.addButton(createFolder);

        this.folderNameInquiry = new GuiTextField(1, this.fontRenderer, 50, this.height - 63 + 20, 200, 20);
        this.folderNameInquiry.setFocused(true);
        this.folderNameInquiry.setText("");
        this.folderNameInquiry.setMaxStringLength(100);
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 2) {
                ((IFolderMenu)parentIn).getDirectoryList().addFolder(this.folderNameInquiry.getText());
        }

        this.mc.displayGuiScreen(this.parentIn);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (this.folderNameInquiry.isFocused()) {
            this.folderNameInquiry.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.folderNameInquiry.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (doDrawScreen) {
            parentIn.drawScreen(mouseX, mouseY, partialTicks);
            this.drawDefaultBackground();
            this.drawString(this.fontRenderer, "FolderEntry Name:", 50, this.height - 63 + 10, -6250336);
            doDrawScreen = false;
        }

        this.folderNameInquiry.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawDefaultBackground() {
        //:: Background
        int background = ColorUtils.getARGB(0, 0, 0, 100);
        this.drawGradientRect(0, 0, this.width, this.height, background, background);
        GuiUtils.drawTexturedRect(0.0D, (double)(this.height - 63), (double)(this.width), (double) this.height, (double)this.zLevel - 1, 64, 64, 64, 255, 0, OPTIONS_BACKGROUND, this.mc);

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
        this.doDrawScreen = true;
        this.parentIn.onResize(mcIn, w, h);
        this.width = w;
        this.height = h;

        this.exit.x = this.width - 30;
        this.createFolder.y = this.height - 63 + 20;
        this.folderNameInquiry.y = this.height - 63 + 20;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.folderNameInquiry.updateCursorCounter();
    }
}
