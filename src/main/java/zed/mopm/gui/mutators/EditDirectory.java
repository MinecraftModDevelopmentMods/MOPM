package zed.mopm.gui.mutators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import zed.mopm.api.data.Editor;
import zed.mopm.api.data.IDrawableListEntry;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.gui.buttons.ContextButton;

import java.io.IOException;

public class EditDirectory<T extends GuiListExtended & IModifiableList> extends GuiScreen {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private GuiScreen parentIn;
    private int entryIndex;
    private boolean canClose;
    private T modifiableList;

    private ContextButton delete;
    private ContextButton rename;
    private ContextButton move;

    private GuiTextField changeName;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public EditDirectory(final GuiScreen parentIn, final int mouseX, final int mouseY, final boolean doClose, final T list) {
        this.parentIn = parentIn;
        this.modifiableList = list;
        this.entryIndex = list.getSlotIndexFromScreenCoords(mouseX, mouseY);
        this.canClose = doClose;

        delete = new ContextButton(Editor.DELETE, mouseX, mouseY, "Delete");
        rename = new ContextButton(Editor.RENAME, mouseX, mouseY + 10, "Rename");
        move = new ContextButton(Editor.CHANGE_DIRECTORY, mouseX, mouseY + 20, "Move");

        IDrawableListEntry temp = (IDrawableListEntry) list.getListEntry(entryIndex);
        changeName = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, temp.getX(), temp.getY(), list.getListWidth(), list.getSlotHeight());
        changeName.setMaxStringLength(Integer.MAX_VALUE);
        changeName.setText(temp.drawableText());
        changeName.setVisible(false);
    }

    public EditDirectory(final GuiScreen parentIn, final int mouseX, final int mouseY, final T list) {
        this(parentIn, mouseX, mouseY, true, list);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiScreen
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        this.addButton(this.delete);
        this.addButton(this.rename);
        this.addButton(this.move);
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.parentIn.drawScreen(mouseX, mouseY, partialTicks);

        if (this.changeName.getVisible()) {
            this.changeName.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(final GuiButton button) {
        Editor action = Editor.values()[button.id];
        switch (action) {
            case DELETE:
                this.modifiableList.delete(this.entryIndex);
                if (canClose) {
                    closeGui();
                }
                break;

            case RENAME:
                this.changeName.setVisible(true);
                this.changeName.setFocused(true);
                break;

            case CHANGE_DIRECTORY:
                this.modifiableList.changeDir(this.entryIndex);
                break;
        }

        this.delete.visible = false;
        this.rename.visible = false;
        this.move.visible = false;
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) {
        if (keyCode == 28) {
            this.modifiableList.rename(this.entryIndex, this.changeName.getText());
            closeGui();
        }
        if (this.changeName.isFocused()) {
            this.changeName.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        if (!(this.delete.mousePressed(this.mc, mouseX, mouseY) || this.rename.mousePressed(this.mc, mouseX, mouseY) || this.move.mousePressed(this.mc, mouseX, mouseY))) {
            closeGui();
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawDefaultBackground() {
        // Do not draw any background
    }

    @Override
    public void onResize(final Minecraft mcIn, final int w, final int h) {
        this.parentIn.onResize(mcIn, w, h);
    }

    @Override
    public void updateScreen() {
        this.changeName.updateCursorCounter();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private void closeGui() {
        this.mc.displayGuiScreen(parentIn);
    }

}
